package wooteco.subway.acceptance.favorite;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import wooteco.subway.AcceptanceTest;
import wooteco.subway.service.favorite.dto.FavoriteResponse;
import wooteco.subway.service.favorite.dto.FavoriteResponses;
import wooteco.subway.service.member.dto.TokenResponse;

public class FavoriteAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        createStation("잠실");
        createStation("석촌고분");
    }

	/*
	given 회원가입이 되어있다.
	given 로그인이 되어있다.
	when 즐겨찾기 등록 요청을 보낸다.
	then 하나가 추가되었다.

	when 즐겨찾기 목록 조회 요청을 한다.
	then 즐겨찾기 목록에 이전에 등록한 즐겨찾기가 있다.

	when 즐겨찾기 삭제 요청을 보낸다.
	then 즐겨찾기 목록이 삭제된다.

	when 즐겨찾기 목록 조회 요청을 한다.
	then 즐겨찾기 목록에 이전에 삭제한 즐겨찾기가 없다.
	*/

    @DisplayName("사용자가 자신의 즐겨찾기 관리한다.")
    @Test
    public void favoriteScenario() {
        String member = createMember(TEST_USER_EMAIL, TEST_USER_NAME, TEST_USER_PASSWORD);
        assertThat(member).isNotBlank();

        TokenResponse tokenResponse = login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
        createFavorite(tokenResponse);

        FavoriteResponses favorites = getFavorites(tokenResponse);
        List<FavoriteResponse> favoriteResponses = favorites.getFavoriteResponses();

        assertThat(favoriteResponses).hasSize(1);
        assertThat(favoriteResponses.get(0).getId()).isNotNull();
        assertThat(favoriteResponses.get(0).getSource()).isEqualTo("잠실");
        assertThat(favoriteResponses.get(0).getTarget()).isEqualTo("석촌고분");

        deleteFavorite(tokenResponse, favoriteResponses.get(0).getId());

        favorites = getFavorites(tokenResponse);
        assertThat(favorites.getFavoriteResponses()).hasSize(0);
    }

    private void deleteFavorite(TokenResponse tokenResponse, long id) {
        given().auth()
            .oauth2(tokenResponse.getAccessToken())
            .when()
            .delete("/favorite/me/" + id)
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());
    }

    private FavoriteResponses getFavorites(TokenResponse tokenResponse) {
        return given().auth()
            .oauth2(tokenResponse.getAccessToken())
            .when()
            .get("/favorite/me")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract().as(FavoriteResponses.class);
    }

    private void createFavorite(TokenResponse tokenResponse) {
        Map<String, String> favoriteRequest = new HashMap<>();
        favoriteRequest.put("source", "잠실");
        favoriteRequest.put("target", "석촌고분");

        given().auth()
            .oauth2(tokenResponse.getAccessToken())
            .body(favoriteRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/favorite/me")
            .then()
            .log().all()
            .statusCode(HttpStatus.CREATED.value());
    }
}
