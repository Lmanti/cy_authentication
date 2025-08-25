package co.com.crediya.cy_authentication.r2dbc.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.domain.Pageable;

import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReactiveUserCriteriaBuilder {
    
    private final R2dbcEntityTemplate template;
    private List<Criteria> criteriaList = new ArrayList<>();
    
    public ReactiveUserCriteriaBuilder withIdNumber(Long idNumber) {
        if (idNumber != null) {
            criteriaList.add(Criteria.where("id_number").is(idNumber));
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            criteriaList.add(Criteria.where("email").is(email));
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withIdType(Integer idType) {
        if (idType != null) {
            criteriaList.add(Criteria.where("id_type").is(idType));
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            criteriaList.add(Criteria.where("name").is(name));
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withLastname(String lastname) {
        if (lastname != null && !lastname.trim().isEmpty()) {
            criteriaList.add(Criteria.where("lastname").is(lastname));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withBirthDate(LocalDate birthDate) {
        if (birthDate != null) {
            criteriaList.add(Criteria.where("birth_date").is(birthDate));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            criteriaList.add(Criteria.where("address").is(address));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            criteriaList.add(Criteria.where("phone").is(phone));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withBaseSalary(Double baseSalary) {
        if (baseSalary != null) {
            criteriaList.add(Criteria.where("base_salary").is(baseSalary));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withBirthDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            criteriaList.add(Criteria.where("birth_date").between(startDate, endDate));
        } else if (startDate != null) {
            criteriaList.add(Criteria.where("birth_date").greaterThanOrEquals(startDate));
        } else if (endDate != null) {
            criteriaList.add(Criteria.where("birth_date").lessThanOrEquals(endDate));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withBaseSalaryBetween(Double minSalary, Double maxSalary) {
        if (minSalary != null && maxSalary != null) {
            criteriaList.add(Criteria.where("base_salary").between(minSalary, maxSalary));
        } else if (minSalary != null) {
            criteriaList.add(Criteria.where("base_salary").greaterThanOrEquals(minSalary));
        } else if (maxSalary != null) {
            criteriaList.add(Criteria.where("base_salary").lessThanOrEquals(maxSalary));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withNameContaining(String namePart) {
        if (namePart != null && !namePart.trim().isEmpty()) {
            criteriaList.add(Criteria.where("name").like("%" + namePart + "%"));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withLastnameContaining(String lastnamePart) {
        if (lastnamePart != null && !lastnamePart.trim().isEmpty()) {
            criteriaList.add(Criteria.where("lastname").like("%" + lastnamePart + "%"));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withAddressContaining(String addressPart) {
        if (addressPart != null && !addressPart.trim().isEmpty()) {
            criteriaList.add(Criteria.where("address").like("%" + addressPart + "%"));
        }
        return this;
    }

    public ReactiveUserCriteriaBuilder withEmailContaining(String emailPart) {
        if (emailPart != null && !emailPart.trim().isEmpty()) {
            criteriaList.add(Criteria.where("email").like("%" + emailPart + "%"));
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withCriteria(Map<String, Object> criteria) {
        if (criteria != null) {
            if (criteria.containsKey("idNumber")) {
                withIdNumber((Long) criteria.get("idNumber"));
            }
            if (criteria.containsKey("email")) {
                withEmail((String) criteria.get("email"));
            }
            if (criteria.containsKey("idType")) {
                withIdType((Integer) criteria.get("idType"));
            }
            if (criteria.containsKey("name")) {
                withName((String) criteria.get("name"));
            }
            if (criteria.containsKey("lastname")) {
                withLastname((String) criteria.get("lastname"));
            }
            // Campos faltantes
            if (criteria.containsKey("birthDate")) {
                withBirthDate((LocalDate) criteria.get("birthDate"));
            }
            if (criteria.containsKey("address")) {
                withAddress((String) criteria.get("address"));
            }
            if (criteria.containsKey("phone")) {
                withPhone((String) criteria.get("phone"));
            }
            if (criteria.containsKey("baseSalary")) {
                withBaseSalary((Double) criteria.get("baseSalary"));
            }
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withUser(User user) {
        if (user != null) {
            withIdNumber(user.getIdNumber());
            withIdType(user.getIdType());
            withName(user.getName());
            withLastname(user.getLastname());
            withBirthDate(user.getBirthDate());
            withAddress(user.getAddress());
            withPhone(user.getPhone());
            withEmail(user.getEmail());
            withBaseSalary(user.getBaseSalary());
        }
        return this;
    }
    
    public ReactiveUserCriteriaBuilder withOrCriteria() {
        if (criteriaList.size() > 1) {
            Criteria orCriteria = criteriaList.get(0);
            for (int i = 1; i < criteriaList.size(); i++) {
                orCriteria = orCriteria.or(criteriaList.get(i));
            }
            criteriaList.clear();
            criteriaList.add(orCriteria);
        }
        return this;
    }
    
    public Flux<UserEntity> find() {
        Criteria criteria = buildCriteria();
        if (criteria == null) {
            return Flux.empty();
        }
        
        Query query = Query.query(criteria);
        return template.select(UserEntity.class)
                      .matching(query)
                      .all();
    }
    
    public Flux<UserEntity> find(Pageable pageable) {
        Criteria criteria = buildCriteria();
        if (criteria == null) {
            return Flux.empty();
        }
        
        Query query = Query.query(criteria)
                          .sort(pageable.getSort())
                          .limit(pageable.getPageSize())
                          .offset(pageable.getOffset());
        
        return template.select(UserEntity.class)
                      .matching(query)
                      .all();
    }
    
    public Mono<Long> count() {
        Criteria criteria = buildCriteria();
        if (criteria == null) {
            return Mono.just(0L);
        }
        
        Query query = Query.query(criteria);
        return template.count(query, UserEntity.class);
    }
    
    private Criteria buildCriteria() {
        if (criteriaList.isEmpty()) {
            return null;
        }
        
        if (criteriaList.size() == 1) {
            return criteriaList.get(0);
        }
        
        Criteria finalCriteria = criteriaList.get(0);
        for (int i = 1; i < criteriaList.size(); i++) {
            finalCriteria = finalCriteria.and(criteriaList.get(i));
        }
        
        return finalCriteria;
    }
}