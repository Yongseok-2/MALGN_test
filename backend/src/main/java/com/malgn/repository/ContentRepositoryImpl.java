package com.malgn.repository;

import com.malgn.domain.Content;
import com.malgn.domain.QContent;
import com.malgn.dto.ContentSearchQueryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory jpaqueryFactory;

    @Override
    public Page<Content> search(ContentSearchQueryDto searchQuery, Pageable pageable) {

        QContent qContent = QContent.content;

        List<Content> contents = jpaqueryFactory
                .selectFrom(qContent)
                .where(
                        searchCondition(searchQuery, qContent),
                        qContent.deleted.isFalse()
                )
                .orderBy(qContent.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaqueryFactory
                .select(qContent.count())
                .from(qContent)
                .where(
                        searchCondition(searchQuery, qContent),
                        qContent.deleted.isFalse()
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private BooleanExpression searchCondition(ContentSearchQueryDto searchQuery, QContent qContent) {
        String keyword = searchQuery.getKeyword();
        ContentSearchQueryDto.SearchType type = searchQuery.getType();

        if (!StringUtils.hasText(keyword) || type == null) {
            return null;
        }

        return switch (type) {
            case TITLE -> qContent.title.contains(keyword);
            case DESCRIPTION -> qContent.description.contains(keyword);
            case CREATED_BY -> qContent.createdBy.eq(keyword);
        };
    }

}
