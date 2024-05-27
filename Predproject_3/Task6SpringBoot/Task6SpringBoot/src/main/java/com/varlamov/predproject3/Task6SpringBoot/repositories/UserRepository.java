package com.varlamov.predproject3.Task6SpringBoot.repositories;


import com.varlamov.predproject3.Task6SpringBoot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
