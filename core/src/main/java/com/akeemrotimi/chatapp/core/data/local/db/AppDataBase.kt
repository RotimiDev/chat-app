package com.akeemrotimi.chatapp.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.akeemrotimi.chatapp.core.common.Converters
import com.akeemrotimi.chatapp.core.data.local.ChatDao
import com.akeemrotimi.chatapp.core.data.local.ChatEntity
import com.akeemrotimi.chatapp.core.data.local.MessageDao
import com.akeemrotimi.chatapp.core.data.local.MessageEntity

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    abstract fun messageDao(): MessageDao
}
