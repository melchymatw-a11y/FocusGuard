package FocusGuard.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import FocusGuard.models.GoalsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GoalsViewModel : ViewModel() {

    var allGoals = mutableStateListOf<GoalsModel>()

    // 1. SAVE GOAL (CREATE)
    fun saveGoal(
        title: String,
        duration: String,
        userId: String,
        navController: NavController,
        context: Context
    ) {
        if (title.isBlank() || duration.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("goals/$userId")
        val goalId = dbRef.push().key ?: ""

        val goal = GoalsModel(
            goalId = goalId,
            userId = userId,
            title = title,
            durationHours = duration
        )

        dbRef.child(goalId).setValue(goal).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Goal set! Stay focused.", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. FETCH ALL GOALS (READ)
    fun fetchGoals(userId: String, context: Context) {
        val dbRef = FirebaseDatabase.getInstance().getReference("goals/$userId")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allGoals.clear()
                for (goalSnapshot in snapshot.children) {
                    val goal = goalSnapshot.getValue(GoalsModel::class.java)
                    goal?.let { allGoals.add(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 3. FETCH SINGLE GOAL (FOR UPDATING)
    fun fetchSingleGoal(userId: String, goalId: String, onResult: (GoalsModel?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("goals/$userId/$goalId")
        dbRef.get().addOnSuccessListener { snapshot ->
            val goal = snapshot.getValue(GoalsModel::class.java)
            onResult(goal)
        }
    }

    // 4. UPDATE GOAL (UPDATE)
    fun updateGoal(
        goalId: String,
        title: String,
        duration: String,
        userId: String,
        navController: NavController,
        context: Context
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("goals/$userId/$goalId")
        val updatedData = mapOf(
            "title" to title,
            "durationHours" to duration
        )

        dbRef.updateChildren(updatedData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Goal updated!", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            } else {
                Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 5. DELETE GOAL (DELETE)
    fun deleteGoal(goalId: String, userId: String, context: Context) {
        val dbRef = FirebaseDatabase.getInstance().getReference("goals/$userId/$goalId")
        dbRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Goal deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- LOGIC FOR SYSTEM ANALYSIS (WORK SESSIONS) ---

    // 6. RECORD WORK SESSION (Saves the time tracked by the timer)
    fun recordWorkSession(
        userId: String,
        goalId: String,
        hoursWorked: Double,
        context: Context
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("sessions/$userId")
        val sessionId = dbRef.push().key ?: ""

        val sessionData = mapOf(
            "sessionId" to sessionId,
            "goalId" to goalId,
            "hours" to hoursWorked,
            "timestamp" to System.currentTimeMillis()
        )

        dbRef.child(sessionId).setValue(sessionData).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(context, "Failed to save progress", Toast.LENGTH_SHORT).show()
            }
        }
    }
}