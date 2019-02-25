package com.omni.omni.util.elasticsearch;

import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RepositoryUtils {

    public static <T> List<T> getAll(ElasticsearchRepository<T, String> repo) {

        long totalCount = repo.count();

        if(totalCount > 900) {

            int pageNum = 0;
            List<T> completeList = new ArrayList<>();

            while(totalCount > 0) {

                completeList.addAll(Lists.newArrayList(repo.findAll(PageRequest.of(pageNum, 900))));

                totalCount -= 900;
                pageNum++;
            }

            return completeList;
        } else {
            return Lists.newArrayList(repo.findAll());
        }
    }
}
