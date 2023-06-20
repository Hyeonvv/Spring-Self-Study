package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello; // static 으로 만들어져있는거 사용 가능
//		QHello qHello = new QHello("h");

		// Querydsl 을 쓸 때 query 와 관련된 건 Q 타입을 넣는다.
		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

		// Querydsl 확인
		assertThat(result).isEqualTo(hello);

		// lombok 동작 확인
		assertThat(result.getId()).isEqualTo(hello.getId());
	}

}
