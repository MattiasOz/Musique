package com.matzuu.musique.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.matzuu.musique.models.Song
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class ListPagingSource(
    private val data: List<Song>
) : PagingSource<Int, Song>() {
    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        // I have no idea what this does
        // Try to get the page containing the last accessed item.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize

        val fromIndex = pageNumber * pageSize
        if (fromIndex >= data.size) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        val toIndex = minOf(fromIndex + pageSize, data.size)
        val sublist = data.subList(fromIndex, toIndex)

        return LoadResult.Page(
            data = sublist,
            prevKey = if (pageNumber <= 0) null else pageNumber - 1,
            nextKey = if (toIndex >= data.size) null else pageNumber + 1
        )
    }
}