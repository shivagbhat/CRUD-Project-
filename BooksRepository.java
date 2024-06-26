package com.firrstproject.Book_Bazaar.services;



import org.springframework.data.jpa.repository.JpaRepository;

import com.firrstproject.Book_Bazaar.models.Books;

public interface BooksRepository extends JpaRepository<Books, Integer> {

}
