package com.example.homies.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.homies.data.source.auth.AuthRepository
import com.example.homies.data.source.auth.AuthRepositoryImpl
import com.example.homies.data.source.db.DbRepository
import com.example.homies.data.source.db.DbRepositoryImpl
import com.example.homies.data.source.localstorage.LocalStorageRepository
import com.example.homies.data.source.localstorage.LocalStorageRepositoryImpl
import com.example.homies.data.source.notification.NotificationRepository
import com.example.homies.data.source.notification.NotificationRepositoryImpl
import com.example.homies.data.source.remotestorage.RemoteStorageRepository
import com.example.homies.data.source.remotestorage.RemoteStorageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
    ): AuthRepository {
        return AuthRepositoryImpl(auth)
    }

    @Singleton
    @Provides
    fun provideDbRepository(
        db: FirebaseFirestore,
        remoteStorageRepository: RemoteStorageRepository,
        authRepository: AuthRepository,
        notificationRepository: NotificationRepository,
        localStorageRepository: LocalStorageRepository,
    ): DbRepository {
        return DbRepositoryImpl(
            db,
            remoteStorageRepository,
            authRepository,
            notificationRepository,
            localStorageRepository
        )
    }

    @Singleton
    @Provides
    fun provideLocalStorageRepository(
        prefs: SharedPreferences,
    ): LocalStorageRepository {
        return LocalStorageRepositoryImpl(prefs)
    }

    @Singleton
    @Provides
    fun provideRemoteStorageRepository(
        storage: FirebaseStorage,
    ): RemoteStorageRepository {
        return RemoteStorageRepositoryImpl(storage)
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(
        localStorageRepository: LocalStorageRepository
    ): NotificationRepository {
        val client = HttpClient(CIO) {
            install(ContentNegotiation)
        }
        // add plugin to client
        return NotificationRepositoryImpl(client, localStorageRepository)
    }

}