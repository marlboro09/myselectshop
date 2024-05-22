package com.sparta.myselectshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;

public interface FolderRepository extends JpaRepository<Folder, Long> {

	List<Folder> findAllByUser(User user);

	List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);
	//select * from folder where user_id = ? and name in ('?', '?', '?');
}
