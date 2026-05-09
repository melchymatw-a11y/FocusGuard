package FocusGuard.models

data class GoalsModel(
    val goalId: String = "",
    val userId: String = "",
    val title: String = "",
    val durationHours: String = "",
    val timestamp: Long = System.currentTimeMillis()
)