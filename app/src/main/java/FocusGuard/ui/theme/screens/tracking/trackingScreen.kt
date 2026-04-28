package FocusGuard.ui.theme.screens.tracking


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun TrackingScreen(navController: NavController){

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrackingScreenPreview(){
    TrackingScreen(rememberNavController())
}