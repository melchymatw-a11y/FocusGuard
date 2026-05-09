package com.example.focusguard.data

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.focusguard.models.Partner
import com.example.focusguard.models.PartnerProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PartnerUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val sentRequests: List<Partner> = emptyList(),
    val receivedRequests: List<Partner> = emptyList(),
    val connectedPartners: List<Partner> = emptyList(),
    val partnerProgressMap: Map<String, PartnerProgress> = emptyMap()
)

class PartnerViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUid get() = auth.currentUser?.uid ?: ""
    private val currentEmail get() = auth.currentUser?.email ?: ""

    private val _uiState = MutableStateFlow(PartnerUiState())
    val uiState: StateFlow<PartnerUiState> = _uiState

    private val listeners = mutableListOf<Pair<DatabaseReference, ValueEventListener>>()

    init {
        listenToPartners()
    }

    fun sendPartnerRequest(toEmail: String) {
        if (toEmail.isBlank()) { setError("Please enter a valid email."); return }
        if (toEmail == currentEmail) { setError("You can't add yourself as a partner."); return }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        db.child("users").orderByChild("email").equalTo(toEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) { setError("No user found with that email."); return }

                    val partnerEntry = snapshot.children.first()
                    val partnerUid = partnerEntry.key ?: run { setError("Could not find user."); return }
                    val partnerName = partnerEntry.child("displayName").getValue(String::class.java) ?: toEmail
                    val currentName = auth.currentUser?.displayName ?: currentEmail

                    val updates = hashMapOf<String, Any>(
                        "partnerships/$partnerUid/received/$currentUid" to hashMapOf(
                            "fromUid" to currentUid,
                            "fromEmail" to currentEmail,
                            "fromName" to currentName,
                            "status" to "pending",
                            "timestamp" to ServerValue.TIMESTAMP
                        ),
                        "partnerships/$currentUid/sent/$partnerUid" to hashMapOf(
                            "toEmail" to toEmail,
                            "toName" to partnerName,
                            "status" to "pending",
                            "timestamp" to ServerValue.TIMESTAMP
                        )
                    )

                    db.updateChildren(updates)
                        .addOnSuccessListener {
                            _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Request sent to $toEmail!")
                        }
                        .addOnFailureListener { e -> setError(e.message ?: "Failed to send request.") }
                }
                override fun onCancelled(error: DatabaseError) { setError(error.message) }
            })
    }

    fun acceptRequest(partner: Partner) {
        val currentName = auth.currentUser?.displayName ?: currentEmail

        val updates = hashMapOf<String, Any?>(
            "partnerships/$currentUid/connected/${partner.uid}" to hashMapOf(
                "email" to partner.email,
                "displayName" to partner.displayName,
                "since" to ServerValue.TIMESTAMP
            ),
            "partnerships/${partner.uid}/connected/$currentUid" to hashMapOf(
                "email" to currentEmail,
                "displayName" to currentName,
                "since" to ServerValue.TIMESTAMP
            ),
            "partnerships/$currentUid/received/${partner.uid}" to null,
            "partnerships/${partner.uid}/sent/$currentUid" to null
        )

        db.updateChildren(updates)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(successMessage = "You and ${partner.email} are now partners!")
            }
            .addOnFailureListener { e -> setError(e.message ?: "Failed to accept request.") }
    }

    fun declineRequest(partner: Partner) {
        val updates = hashMapOf<String, Any?>(
            "partnerships/$currentUid/received/${partner.uid}" to null,
            "partnerships/${partner.uid}/sent/$currentUid" to null
        )
        db.updateChildren(updates)
    }

    fun removePartner(partner: Partner) {
        val updates = hashMapOf<String, Any?>(
            "partnerships/$currentUid/connected/${partner.uid}" to null,
            "partnerships/${partner.uid}/connected/$currentUid" to null
        )
        db.updateChildren(updates)
    }

    private fun listenToPartners() {
        if (currentUid.isEmpty()) return

        val receivedRef = db.child("partnerships/$currentUid/received")
        val receivedListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    Partner(
                        uid = child.child("fromUid").getValue(String::class.java) ?: child.key ?: "",
                        email = child.child("fromEmail").getValue(String::class.java) ?: "",
                        displayName = child.child("fromName").getValue(String::class.java) ?: "",
                        status = "pending"
                    )
                }
                _uiState.value = _uiState.value.copy(receivedRequests = list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        receivedRef.addValueEventListener(receivedListener)
        listeners.add(receivedRef to receivedListener)

        val sentRef = db.child("partnerships/$currentUid/sent")
        val sentListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    Partner(
                        uid = child.key ?: "",
                        email = child.child("toEmail").getValue(String::class.java) ?: "",
                        displayName = child.child("toName").getValue(String::class.java) ?: "",
                        status = "pending"
                    )
                }
                _uiState.value = _uiState.value.copy(sentRequests = list)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        sentRef.addValueEventListener(sentListener)
        listeners.add(sentRef to sentListener)

        val connectedRef = db.child("partnerships/$currentUid/connected")
        val connectedListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    Partner(
                        uid = child.key ?: "",
                        email = child.child("email").getValue(String::class.java) ?: "",
                        displayName = child.child("displayName").getValue(String::class.java) ?: "",
                        status = "connected",
                        since = child.child("since").getValue(Long::class.java) ?: 0L
                    )
                }
                _uiState.value = _uiState.value.copy(connectedPartners = list)
                list.forEach { listenToPartnerProgress(it.uid) }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        connectedRef.addValueEventListener(connectedListener)
        listeners.add(connectedRef to connectedListener)
    }

    private fun listenToPartnerProgress(partnerUid: String) {
        val sessionsRef = db.child("sessions/$partnerUid")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalMinutes = 0L
                var sessionCount = 0
                snapshot.children.forEach { session ->
                    totalMinutes += session.child("durationMinutes").getValue(Long::class.java) ?: 0L
                    sessionCount++
                }
                db.child("goals/$partnerUid").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(goalSnap: DataSnapshot) {
                        val partner = _uiState.value.connectedPartners.find { it.uid == partnerUid }
                        val progress = PartnerProgress(
                            partnerId = partnerUid,
                            partnerEmail = partner?.email ?: "",
                            totalGoals = goalSnap.childrenCount.toInt(),
                            completedSessions = sessionCount,
                            totalFocusMinutes = totalMinutes
                        )
                        val updatedMap = _uiState.value.partnerProgressMap.toMutableMap()
                        updatedMap[partnerUid] = progress
                        _uiState.value = _uiState.value.copy(partnerProgressMap = updatedMap)
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        sessionsRef.addValueEventListener(listener)
        listeners.add(sessionsRef to listener)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
    }

    override fun onCleared() {
        super.onCleared()
        listeners.forEach { (ref, listener) -> ref.removeEventListener(listener) }
    }
}