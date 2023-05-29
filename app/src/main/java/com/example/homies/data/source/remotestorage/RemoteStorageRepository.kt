package com.example.homies.data.source.remotestorage

import android.net.Uri
import com.example.homies.data.model.ImagePath

interface RemoteStorageRepository {
    suspend fun getDownloadUrl(imagePath: ImagePath): String
    suspend fun uploadImage(uri: Uri, fileName: String): ImagePath
}