package com.example.eco_plant.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.eco_plant.R

// Define the Inter font family
val InterFontFamily = FontFamily(
    Font(R.font.inter_24pt_extrabold, FontWeight.ExtraBold), //ExtraBold
    Font(R.font.inter_18pt_semibold, FontWeight.SemiBold), //SemiBold
    Font(R.font.inter_18pt_bold, FontWeight.Bold), //Bold
    Font(R.font.inter_18pt_regular, FontWeight.Normal) //Regular
)

// Define typography categories
// Headings category -> done
object Heading {
    val H1 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold
    )
    val H2 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
    )
    val H3 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold
    )
    val H4 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    val H5 = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

}

// Body category -> done
object Body {
    val XL = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    )
    val L = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
    val M = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
    val S = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    )
    val XS = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )

}

// Action category -> done
object Action {
    val L = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    val M = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )
    val S = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
    )
}


// Combine all styles into a single Typography object
val InterTypography = androidx.compose.material3.Typography(
    //Headings
    displayLarge = Heading.H1,
    displayMedium = Heading.H2,
    displaySmall = Heading.H3,
    headlineLarge = Heading.H4,
    headlineMedium = Heading.H5,
    //Body
    bodyLarge = Body.XL,
    bodyMedium = Body.L,
    bodySmall = Body.M,
    //Action
    labelLarge = Action.L,
    labelMedium = Action.M,
    labelSmall = Action.S,



)