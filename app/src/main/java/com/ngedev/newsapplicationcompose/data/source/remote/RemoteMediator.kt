package com.ngedev.newsapplicationcompose.data.source.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ngedev.newsapplicationcompose.domain.model.RemoteKeys
import com.ngedev.newsapplicationcompose.utils.wrapEspressoIdlingResource

@OptIn(ExperimentalPagingApi::class)
abstract class RemoteMediator<RequestType : Any, ResponseType, LocalSource> :
    RemoteMediator<Int, RequestType>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RequestType>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            try {
                val response = createCall(page, state.config.pageSize)
                return MediatorResult.Success(
                    createDatabaseWithTransaction(
                        database,
                        loadType,
                        response,
                        page
                    )
                )
            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    protected abstract suspend fun getRemoteKeyForLastItem(state: PagingState<Int, RequestType>): RemoteKeys?

    protected abstract suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, RequestType>): RemoteKeys?

    protected abstract suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, RequestType>): RemoteKeys?

    abstract suspend fun createCall(page: Int, size: Int): ResponseType

    abstract val database: LocalSource

    abstract suspend fun createDatabaseWithTransaction(
        database: LocalSource,
        loadType: LoadType,
        response: ResponseType,
        page: Int
    ): Boolean


}