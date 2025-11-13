package com.example.camerastyle.di

import android.content.Context
import com.example.camerastyle.data.local.StyleDataSource
import com.example.camerastyle.data.repository.ImageRepository
import com.example.camerastyle.data.repository.StyleRepository
import com.example.camerastyle.domain.processor.ImageProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 应用级依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStyleDataSource(): StyleDataSource {
        return StyleDataSource()
    }

    @Provides
    @Singleton
    fun provideStyleRepository(
        styleDataSource: StyleDataSource
    ): StyleRepository {
        return StyleRepository(styleDataSource)
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context
    ): ImageRepository {
        return ImageRepository(context)
    }

    @Provides
    @Singleton
    fun provideImageProcessor(): ImageProcessor {
        return ImageProcessor()
    }
}
