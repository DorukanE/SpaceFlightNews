package com.dorukaneskiceri.spaceflightnews.util

import com.dorukaneskiceri.spaceflightnews.data.model.ArticleResponse
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class BaseResultTypeAdapter : JsonSerializer<BaseResult<*>>, JsonDeserializer<BaseResult<*>> {

    override fun serialize(
        src: BaseResult<*>,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val json = JsonObject()
        when (src) {
            is BaseResult.Success<*> -> {
                json.addProperty("type", "success")
                json.add("data", context.serialize(src.data))
            }

            is BaseResult.Error -> {
                json.addProperty("type", "error")
                json.addProperty("exception", src.exception.message)
            }
        }
        return json
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BaseResult<*> {
        val gson = Gson()
        val articleResponseType = object : TypeToken<ArticleResponse>() {}.type

        return try {
            val articleResponse = gson.fromJson<ArticleResponse>(json, articleResponseType)
            BaseResult.Success(articleResponse)
        } catch (e: Exception) {
            BaseResult.Error(e)
        }
    }
}