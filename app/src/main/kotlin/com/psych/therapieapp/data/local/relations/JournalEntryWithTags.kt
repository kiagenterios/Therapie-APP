package com.psych.therapieapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.psych.therapieapp.data.local.entity.JournalEntry
import com.psych.therapieapp.data.local.entity.JournalEntryTagCrossRef
import com.psych.therapieapp.data.local.entity.JournalTag

data class JournalEntryWithTags(
    @Embedded
    val entry: JournalEntry,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = JournalEntryTagCrossRef::class,
            parentColumn = "entryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<JournalTag>
)
