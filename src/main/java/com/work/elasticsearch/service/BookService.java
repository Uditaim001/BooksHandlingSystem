package com.work.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.work.elasticsearch.entity.Books;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final ElasticsearchClient elasticsearchClient;

    public BookService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public Books addBook(Books book) throws IOException {
        try {
            if (book.getId() == null) {
                book.setId(UUID.randomUUID().toString());
            }
            elasticsearchClient.index(i -> i
                    .index("books")
                    .id(book.getId())
                    .document(book)
            );
            return book;
        } catch (IOException e) {
            // Log and rethrow exception
            throw new IOException("Failed to add book to Elasticsearch", e);
        }
    }

    public List<Books> searchBooksByTitle(String title) throws IOException {
        try {
            Query query = MatchQuery.of(m -> m
                    .field("title")
                    .query(title)
            )._toQuery();

            return elasticsearchClient.search(s -> s
                            .index("books")
                            .query(query), Books.class)
                    .hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Failed to search books by title", e);
        }
    }

    public List<Books> searchBooksByAuthor(String author) throws IOException {
        try {
            Query query = MatchQuery.of(m -> m
                    .field("author")
                    .query(author)
            )._toQuery();

            return elasticsearchClient.search(s -> s
                            .index("books")
                            .query(query), Books.class)
                    .hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Failed to search books by author", e);
        }
    }

    public List<Books> getAllBooks() throws IOException {
        try {
            return elasticsearchClient.search(s -> s
                                    .index("books")
                                    .query(q -> q.matchAll(m -> m)), // Match all documents
                            Books.class)
                    .hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Failed to retrieve all books", e);
        }
    }

    public Optional<Books> getBookById(String id) throws IOException {
        try {
            return Optional.ofNullable(
                    elasticsearchClient.get(g -> g
                                    .index("books")
                                    .id(id), Books.class)
                            .source()
            );
        } catch (IOException e) {
            throw new IOException("Failed to retrieve book by ID", e);
        }
    }
}