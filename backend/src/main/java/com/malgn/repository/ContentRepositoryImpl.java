package com.malgn.repository;

import com.malgn.domain.Content;
import com.malgn.domain.QContent;
import com.malgn.dto.ContentSearchQuery;
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
    public Page<Content> search(ContentSearchQuery searchQuery, Pageable pageable) {

        QContent qContent = QContent.content;

        // 실제 데이터를 가져옴 (페이징 적용)
        List<Content> contents = jpaqueryFactory
                .selectFrom(qContent)
                .where(
                        titleContains(searchQuery.getTitle(), qContent),
                        textContains(searchQuery.getDescription(), qContent),
                        createdByEq(searchQuery.getCreatedBy(), qContent),
                        qContent.deleted.eq(false)
                )
                .orderBy(qContent.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 데이터 개수
        JPAQuery<Long> countQuery = jpaqueryFactory
                .select(qContent.count())
                .from(qContent)
                .where(
                        titleContains(searchQuery.getTitle(), qContent),
                        textContains(searchQuery.getDescription(), qContent),
                        createdByEq(searchQuery.getCreatedBy(), qContent),
                        qContent.deleted.eq(false)
                );
        // Page객체로 변환
        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleContains(String title, QContent qContent) {
        return StringUtils.hasText(title) ? qContent.title.contains(title) : null;
    }

    private BooleanExpression textContains(String description, QContent qContent) {
        return StringUtils.hasText(description) ? qContent.description.contains(description) : null;
    }

    private BooleanExpression createdByEq(String createdBy, QContent qContent) {
        return StringUtils.hasText(createdBy) ? qContent.createdBy.eq(createdBy) : null;
    }

}
