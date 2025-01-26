package com.example.talkk.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.talkk.R

@Composable
fun SignInScreenUI(onSignInClick: () -> Unit) {
    val brush = Brush.linearGradient(
        listOf(
            Color(0xFF238CDD), Color(0xFF255DCC)
        )
    )
    Image(
        painter = painterResource(id = R.drawable.login_blur),
        contentDescription = "background image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(80.dp))
        Image(
            painter = painterResource(id = R.drawable.oig4__rndcloiljdx4hxpn),
            contentDescription = ""
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(70.dp))

        Button(
            onClick = { onSignInClick() },
            modifier = Modifier
                .background(brush, CircleShape)
                .fillMaxWidth(0.7f)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            shape = CircleShape
        ) {
            Text(
                text = "Continue With Google",
                color = Color.White,
                modifier = Modifier.padding(end = 20.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Icon(
                painter = painterResource(id = R.drawable.goog_0ed88f7c),
                contentDescription = "",
                modifier = Modifier.scale(1.2f)
            )

        }


    }
}