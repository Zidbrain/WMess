package com.example.wmess.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.wmess.R

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DecoratedTextInputField(modifier: Modifier = Modifier) {
    ConstraintLayout(modifier = modifier) {
        val (tf, bt) = remember { createRefs() }

        OutlinedTextField(value = "dad", onValueChange = {}, modifier = Modifier.constrainAs(tf) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        })
        OutlinedIconButton(
            onClick = { /*TODO*/ }, modifier = Modifier
                .padding(end = 20.dp)
                .constrainAs(bt) {
                    end.linkTo(parent.end)
                    centerAround(tf.bottom)
                }
                .size(16.dp)
        ) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_edit_24),
                contentDescription = null,
            )
        }
    }
}