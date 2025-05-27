import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.SecureFlagPolicy
import com.dorukaneskiceri.spaceflightnews.R
import com.dorukaneskiceri.spaceflightnews.ui.theme.SpaceFlightNewsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorBottomSheet(
    title: String,
    isShowBottomSheet: Boolean,
    bottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(
        securePolicy = SecureFlagPolicy.Inherit,
        shouldDismissOnBackPress = true,
    ),
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    onDismissRequest: () -> Unit = {},
    onButtonClick: () -> Unit
) {
    if (isShowBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = Color.White,
            sheetState = sheetState,
            properties = bottomSheetProperties
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = "Error Icon",
                )
                Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.medium))
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.medium))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(SpaceFlightNewsTheme.spacing.large))
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = SpaceFlightNewsTheme.spacing.medium,
                            end = SpaceFlightNewsTheme.spacing.medium,
                            bottom = SpaceFlightNewsTheme.spacing.large
                        )
                ) {
                    Text("Okay")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ErrorBottomSheetPreview() {
    ErrorBottomSheet(
        title = "Error",
        isShowBottomSheet = true,
        sheetState = rememberModalBottomSheetState(),
        onButtonClick = {}
    )
}