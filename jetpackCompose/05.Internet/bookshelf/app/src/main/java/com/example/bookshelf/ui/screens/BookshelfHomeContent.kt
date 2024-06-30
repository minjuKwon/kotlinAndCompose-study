package com.example.bookshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.R
import com.example.bookshelf.checkBookmarkList
import com.example.bookshelf.checkCurrentItem
import com.example.bookshelf.checkTabPressed
import com.example.bookshelf.data.BookType
import com.example.bookshelf.getTotalItemsCount
import com.example.bookshelf.network.Book
import com.example.bookshelf.network.BookInfo
import com.example.bookshelf.ui.BookshelfUiState
import com.example.bookshelf.ui.PAGE_SIZE

@Composable
fun BookshelfListOnlyContent(
    books:LazyPagingItems<Book>,
    bookshelfUiState: BookshelfUiState,
    onSearch:(String)->Unit,
    onBookItemPressed: (BookInfo) -> Unit,
    input:String,
    onInputChange:(String)->Unit,
    onInputReset:(String)->Unit,
    onBookmarkPressed:(Book)->Unit,
    currentPage:Int,
    updatePage:(Int)->Unit,
    modifier:Modifier= Modifier,
    scrollState:LazyListState
){
    Column(
        modifier= Modifier
            .padding(dimensionResource(R.dimen.list_only_content_column_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val totalCount= getTotalItemsCount(bookshelfUiState)
        val pageSize = PAGE_SIZE
        val totalPages = (totalCount+pageSize-1)/pageSize
        val pageGroupSize = 3
        val currentGroup = (currentPage-1)/pageGroupSize

        SearchTextField(
            input = input,
            onInputChange = onInputChange,
            onSearch = onSearch,
            onInputReset = onInputReset
        )

        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(
                        R.dimen.list_only_content_total_text_top_padding
                    ),
                    start = dimensionResource(
                        R.dimen.list_only_content_total_text_start_padding
                    )
                ),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.totalCount))
            Text(text = " $totalCount")
        }

        LazyColumn(
            state=scrollState,
            modifier= Modifier
                .padding(dimensionResource(R.dimen.list_padding))
                .fillMaxHeight(0.9f)
        ){
            if(checkTabPressed(bookshelfUiState)==BookType.Bookmark){
                    items(checkBookmarkList(bookshelfUiState),key={it.id}){
                        BookShelfListItem(
                            book = it,
                            onBookItemPressed=onBookItemPressed,
                            onBookMarkPressed = onBookmarkPressed,
                            modifier=modifier
                        )
                    }
            }else{
                items(count=books.itemCount){
                    books[it]?.let { it1 ->
                        BookShelfListItem(
                            book = it1,
                            onBookItemPressed=onBookItemPressed,
                            onBookMarkPressed = onBookmarkPressed,
                            modifier=modifier
                        )
                    }
                }
                item{
                    books.apply  {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier=Modifier.fillMaxWidth()
                        ){
                            when{
                                loadState.refresh is LoadState.Loading -> {
                                    CircularProgressIndicator()
                                }
                                loadState.refresh is LoadState.Error ->{
                                    Text(stringResource(R.string.load_data_error))
                                }
                                loadState.append is LoadState.Loading -> {
                                    CircularProgressIndicator()
                                }
                                loadState.append is LoadState.Error -> {
                                    Text(stringResource(R.string.load_data_error))
                                }
                            }
                        }
                    }
                }
                item {
                    PageNumberButton(
                        currentGroup = currentGroup,
                        pageGroupSize = pageGroupSize,
                        totalPages = totalPages,
                        updatePage = updatePage,
                        currentPage = currentPage
                    )
                }
            }
        }

    }
}

@Composable
fun BookshelfListAndDetailContent(
    books:LazyPagingItems<Book>,
    bookshelfUiState: BookshelfUiState,
    onSearch: (String) -> Unit,
    onBookItemPressed: (BookInfo) -> Unit,
    onBackPressed:()->Unit,
    input:String,
    onInputChange:(String)->Unit,
    onInputReset:(String)->Unit,
    onBookmarkPressed:(Book)->Unit,
    currentPage: Int,
    updatePage: (Int) -> Unit,
    scrollState: LazyListState,
    modifier:Modifier= Modifier
){
    Row(modifier=modifier){
        BookshelfListOnlyContent(
            books = books,
            bookshelfUiState = bookshelfUiState,
            onSearch=onSearch,
            onBookItemPressed = onBookItemPressed,
            input=input,
            onInputChange=onInputChange,
            onInputReset=onInputReset,
            onBookmarkPressed=onBookmarkPressed,
            currentPage=currentPage,
            updatePage=updatePage,
            scrollState=scrollState
        )
        BookshelfDetailsScreen(
            book = checkCurrentItem(bookshelfUiState),
            onBackPressed=onBackPressed
        )
    }
}

@Composable
fun BookmarkEmptyScreen(modifier:Modifier=Modifier){
    Box(
        modifier=modifier,
        contentAlignment = Alignment.Center
    ){
        Text(
            text=stringResource(R.string.empty_bookmark),
            textAlign=TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextField(
    input:String,
    onInputChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onInputReset: (String) -> Unit
){
    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        label={Text(stringResource(R.string.search_label))},
        leadingIcon = {
            Icon(
                imageVector= Icons.Filled.Search,
                contentDescription=stringResource(R.string.search),
                modifier= Modifier
                    .clickable { onSearch(input) }
                    .padding(
                        dimensionResource(R.dimen.list_only_content_search_icon_padding)
                    )
            )
        },
        trailingIcon={
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.search_close),
                modifier= Modifier
                    .clickable { onInputReset("") }
                    .padding(
                        dimensionResource(R.dimen.list_only_content_search_icon_padding)
                    )
            )
        },
        keyboardOptions= KeyboardOptions.Default.copy(
            imeAction= ImeAction.Search
        ),
        keyboardActions= KeyboardActions(
            onSearch = {onSearch(input)},
        ),
        modifier= Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(
                    R.dimen.list_only_content_search_horizontal_padding
                )
            )
    )
}

@Composable
private fun PageNumberButton(
    currentGroup:Int,
    pageGroupSize:Int,
    totalPages:Int,
    updatePage: (Int) -> Unit,
    currentPage: Int
) {
    Row(
        horizontalArrangement =Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.page_button_padding))
            .fillMaxWidth()
    ) {
        val startPage = currentGroup * pageGroupSize + 1
        val endPage = minOf(startPage + pageGroupSize - 1, totalPages)
        if (currentGroup > 0) {
            Text(
                text=stringResource(R.string.previous_page),
                modifier=Modifier
                    .clickable { updatePage(startPage - pageGroupSize) }
            )
        }
        for (page in startPage..endPage) {
            Text(
                text=page.toString(),
                color = if (page == currentPage) Color.Black else Color.LightGray,
                modifier=Modifier
                    .clickable { updatePage(page) }
            )
        }
        if (endPage < totalPages) {
            Text(
                text=stringResource(R.string.next_page),
                modifier=Modifier
                    .clickable { updatePage(startPage + pageGroupSize) }
            )
        }
    }
}

@Composable
private fun BookShelfListItem(
    book: Book,
    onBookItemPressed:(BookInfo)->Unit,
    onBookMarkPressed:(Book)->Unit,
    modifier:Modifier=Modifier
){
    var isBookmarked by remember{mutableStateOf(book.bookInfo.isBookmarked)}
    Row(
        modifier= Modifier
            .clickable { onBookItemPressed(book.bookInfo) }
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.list_item_padding))
            .background(
                Color(
                    ContextCompat.getColor(LocalContext.current, R.color.light_gray)
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier=Modifier
                .padding(dimensionResource(R.dimen.list_item_image_padding))
        ){
            book.bookInfo.img?.let {
                AsyncImage(
                    model=ImageRequest.Builder(context=LocalContext.current)
                            .data(it.thumbnail).build(),
                    contentDescription = null,
                    contentScale= ContentScale.FillBounds,
                    modifier= Modifier
                        .height(dimensionResource(R.dimen.list_item_image_height))
                        .width(dimensionResource(R.dimen.list_item_image_width))
                )
            }
        }

        Column(
            modifier=Modifier.padding(dimensionResource(R.dimen.list_item_text_column_padding))
        ) {
            ItemDescription(book = book,modifier=modifier)
            IconButton(
                onClick = {isBookmarked=!isBookmarked
                onBookMarkPressed(book)}
            ) {
                Icon(
                    imageVector = if(isBookmarked){Icons.Default.Bookmark}
                    else {Icons.Default.BookmarkBorder},
                    contentDescription = stringResource(R.string.bookmark),
                )
            }
        }

    }
}

@Composable
private fun ItemDescription(
    book:Book,
    modifier:Modifier=Modifier
){
    book.bookInfo.title?.let {
        Text(
            text= it,
            style = MaterialTheme.typography.bodyLarge,
            modifier=Modifier.padding(
                bottom=dimensionResource(R.dimen.list_item_text_padding)
            )
        )
    }
    Row(modifier=modifier){
        book.bookInfo.authors?.forEach{
            Text(
                text="$it ",
                style=MaterialTheme.typography.bodySmall,
                modifier=Modifier.padding(
                    bottom=dimensionResource(R.dimen.list_item_text_padding)
                )
            )
        }
    }
    book.bookInfo.publisher?.let {
        Text(
            text= it,
            style=MaterialTheme.typography.bodySmall,
            modifier=Modifier.padding(
                bottom=dimensionResource(R.dimen.list_item_text_padding)
            )
        )
    }
    book.bookInfo.publishedDate?.let {
        Text(
            text= it,
            style=MaterialTheme.typography.bodySmall,
            modifier=modifier
        )
    }
}