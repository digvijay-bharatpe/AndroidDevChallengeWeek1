package com.example.petadoptionapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import dev.chrisbanes.accompanist.glide.GlideImage
import java.io.InputStreamReader
import java.lang.StringBuilder

class PetListActivity : AppCompatActivity() {

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetListScreen(getPetsList(this))
        }
    }
}

private val fillWidthHeight: Modifier = Modifier
    .fillMaxHeight()
    .fillMaxWidth()

@ExperimentalFoundationApi
@Composable
fun PetListScreen(petsList: List<PetDetailInfo>) {
    val navigationController = rememberNavController()
    var petDetail: PetDetailInfo = getFakePetDetail()
    NavHost(navController = navigationController, startDestination = "list") {
        composable("list") {
            Box(modifier = fillWidthHeight) {
                Column(
                    modifier = fillWidthHeight
                ) {
                    PetTopAppBar(title = "Pets Listing")
                    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                        this.items(items = petsList) { petInfo ->
                            PetBasicInfoView(petInfo) {
                                petDetail = it
                                navigationController.navigate("detail") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }
            }
        }
        composable("detail") {
            PetDetailView(info = petDetail)
        }
    }
}


@Composable
fun PetBasicInfoView(petDetailInfo: PetDetailInfo, clickCallback: (PetDetailInfo) -> Unit) {
    val rememberedInfo = remember { petDetailInfo }
    Card(
        elevation = 8.dp, modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                GlideImage(
                    data = petDetailInfo.pictureUrl ?: "",
                    fadeIn = true,
                    contentDescription = "pet image"
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = petDetailInfo.ageDescription ?: "",
                    style = TextStyle(color = Color(0x15, 0x5C, 0x78)),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            Text(
                petDetailInfo.breed ?: "",
                style = TextStyle(
                    color = Color(164, 53, 245),
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                maxLines = 1
            )

            Button(
                onClick = {
                    Log.d("PetInfo", rememberedInfo.toString())
                    clickCallback(rememberedInfo)
                },
                Modifier
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = Color(0x66, 0x27, 0xE6),
                        shape = RoundedCornerShape(5.dp)
                    )
            ) {
                Text(
                    text = "View Details",
                    modifier = Modifier.padding(
                        start = 2.dp,
                        end = 2.dp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}


@Preview
@Composable
fun ShowPreview() {
//    val petInfo = PetDetailInfo()
    PetDetailView(info = getFakePetDetail())
}

@Composable
fun PetTopAppBar(title: String) {
    TopAppBar {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 16.dp),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

private val titleColor = Color(0x66, 0x00, 0xFF)

private val titleStyle = TextStyle(
    color = titleColor,
    fontSize = 16.sp,
    fontWeight = FontWeight.SemiBold
)
private val infoStyle = TextStyle(
    color = Color(0x80, 0x80, 0x80),
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal
)

@Composable
fun PetDetailView(info: PetDetailInfo) {
    Box(
        modifier = fillWidthHeight
    ) {
        Column {
            PetTopAppBar(title = info.breed ?: "")
            Column(modifier = Modifier.verticalScroll(ScrollState(0))) {

                Text(
                    text = info.petsName ?: "",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, top = 16.dp),
                    style = titleStyle
                )
                Text(
                    text = "${info.ageDescription}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 6.dp, bottom = 16.dp),
                    style = infoStyle
                )

                GlideImage(
                    data = info.pictureUrl ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    fadeIn = true,
                    contentDescription = "pets image"
                ) {

                }

                Text(
                    text = "Address",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, top = 16.dp),
                    style = titleStyle
                )
                Text(
                    text = "${info.nameOfOwner}\n${info.currentAddress}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp),
                    style = infoStyle
                )

                Spacer(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = titleColor)
                )

                Text(
                    text = "About",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, top = 8.dp),
                    style = titleStyle
                )
                Text(
                    text = info.description ?: "",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(start = 16.dp, end = 16.dp),
                    style = infoStyle
                )
                Row {
                    Button(
                        onClick = {

                        },
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 32.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                color = Color(0x66, 0x27, 0xE6),
                                shape = RoundedCornerShape(5.dp)
                            )
                    ) {
                        Text(
                            text = "Confirm Adoption",
                            modifier = Modifier.padding(
                                start = 2.dp,
                                end = 2.dp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

class PetDetailInfo {

    @SerializedName("ageDescription")
    var ageDescription: String? = null

    @SerializedName("breed")
    var breed: String? = null

    @SerializedName("nameOfOwner")
    var nameOfOwner: String? = null

    @SerializedName("currentAddress")
    var currentAddress: String? = null

    @SerializedName("pictureUrl")
    var pictureUrl: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("petsName")
    var petsName: String? = null

    override fun toString(): String {
        return "${petsName}, $breed"
    }

}

private fun getPetsList(context: Context): List<PetDetailInfo> {
    val gson = Gson()
    val inStream = InputStreamReader(
        context.assets.open("pets.json")
    )
    val sb = StringBuilder()
    inStream.readLines().forEach {
        sb.append(it)
    }
    return gson.fromJson(sb.toString(), object : TypeToken<List<PetDetailInfo>>() {}.type)
}

private fun getFakePetDetail(): PetDetailInfo {
    return PetDetailInfo().apply {
        description = "very cute pet name muffine, 6 months old, labra breed not price of adoption."
        pictureUrl = "https://picsum.photos/300/300"
        breed = "German Shepherd"
        ageDescription = "6 months old"
        petsName = "Bruno"
        nameOfOwner = "Ram Laal Rastogi"
        currentAddress = "Gali no 55, Chandni chowk, New Delhi- 110058"
    }
}


