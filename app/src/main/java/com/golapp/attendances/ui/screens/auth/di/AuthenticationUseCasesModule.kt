package com.golapp.attendances.ui.screens.auth.di

import com.golapp.attendances.domain.repository.AuthRepository
import com.golapp.attendances.ui.screens.auth.usecases.AuthenticateWithEmailUseCaseImpl
import com.golapp.attendances.ui.screens.auth.usecases.AuthenticationUseCases
import com.golapp.attendances.ui.screens.auth.usecases.ValidateEmailUseCaseImpl
import com.golapp.attendances.ui.screens.auth.usecases.ValidatePasswordUseCaseImpl
import com.golapp.attendances.ui.screens.auth.usecases.ValidateTokenExpiryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationUseCasesModule {

    @Provides
    @Singleton
    fun provideAuthenticationUseCases(loginRepository: AuthRepository): AuthenticationUseCases {
        return AuthenticationUseCases(
            validateEmail = ValidateEmailUseCaseImpl(),
            validatePassword = ValidatePasswordUseCaseImpl(),
            loginWithEmail = AuthenticateWithEmailUseCaseImpl(loginRepository),
            validateTokenExpiry = ValidateTokenExpiryImpl(loginRepository)
        )
    }
}
