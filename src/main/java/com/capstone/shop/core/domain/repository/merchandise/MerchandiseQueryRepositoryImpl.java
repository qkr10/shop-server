package com.capstone.shop.core.domain.repository.merchandise;


import com.capstone.shop.core.domain.entity.Category;
import com.capstone.shop.core.domain.entity.Merchandise;

import com.capstone.shop.core.domain.entity.QCategory;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.capstone.shop.core.domain.entity.QCategory.category;
import static com.capstone.shop.core.domain.entity.QMerchandise.merchandise;

@RequiredArgsConstructor
@Repository
public class MerchandiseQueryRepositoryImpl implements MerchandiseQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Merchandise> findRelatedMerchandises(Merchandise entity) {
        //현재 상품의 {대카테고리, 중카테고리, 소카테고리} 리스트. 소카테고리와 중카테고리는 없을수도 있음. 예시) {기타}, {전자제품, 기타}
        List<Category> categorieList = entity.getCategory().getCategoryList();

        List<Merchandise> result = new ArrayList<>();
        int relatedMerchandise = 6;
        for (int i = 0; i < categorieList.size(); i++) {
            //1. 현재 상품과 관련된 카테고리를 조회한다.
            QCategory children = new QCategory("children");
            QCategory children2 = new QCategory("children2");
            QCategory select = new QCategory[]{category, children, children2}[i];
            List<Function<JPAQuery<Long>, JPAQuery<Long>>> joins = List.of(
                    (JPAQuery<Long> q) -> q.leftJoin(category.children, children),
                    (JPAQuery<Long> q) -> q.leftJoin(children.children, children2)
            );

            JPAQuery<Long> query = queryFactory
                    .select(select.id)
                    .from(category);

            for (int j = 0; j < i; j++)
                query = joins.get(j).apply(query);

            int indexFromBack = categorieList.size() - 1 - i;
            Long currentCategoryId = categorieList.get(indexFromBack).getId();
            List<Long> categoryIdList = query
                    .where(category.id.eq(currentCategoryId))
                    .fetch();

            //2. 1에서 찾은 카테고리로 관련 상품을 조회한다.
            List<Merchandise> merchandiseList = queryFactory
                    .select(merchandise)
                    .from(merchandise)
                    .where(merchandise.category.id.in(categoryIdList))
                    .where(merchandise.id.ne(entity.getId()))
                    .orderBy(merchandise.wish.desc())
                    .limit(relatedMerchandise)
                    .fetch();
            result.addAll(merchandiseList);

            //3. 필요한 갯수만큼 찾았으면 종료
            relatedMerchandise -= merchandiseList.size();
            if (relatedMerchandise == 0)
                break;
        }
        return result;
    }
}
