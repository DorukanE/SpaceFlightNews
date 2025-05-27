import android.net.Uri
import com.google.gson.Gson

inline fun <reified T> T.customToJson(): String {
    return Uri.encode(Gson().toJson(this).replace("#", "%23"))
}