package FocusGuard.ui.theme.screens.goals


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun AddGoalScreen(navController: NavController){

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddGoalScreenPreview(){
    AddGoalScreen(rememberNavController())
}