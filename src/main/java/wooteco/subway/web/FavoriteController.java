package wooteco.subway.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.domain.favorite.Favorite;
import wooteco.subway.domain.member.Member;
import wooteco.subway.service.favorite.FavoriteService;
import wooteco.subway.service.favorite.dto.FavoriteRequest;
import wooteco.subway.service.favorite.dto.FavoriteResponse;
import wooteco.subway.service.favorite.dto.FavoriteResponses;
import wooteco.subway.web.member.LoginMember;

@RestController
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/favorite/me")
    public ResponseEntity<Void> createFavorite(@LoginMember Member member,
        @RequestBody FavoriteRequest favoriteRequest) {
        Favorite persistFavorite = favoriteService.createFavorite(favoriteRequest.toFavorite(member.getId()));
        return ResponseEntity.created(URI.create("/favorite/me/" + persistFavorite.getId())).build();
    }

    @GetMapping("/favorite/me")
    public ResponseEntity<FavoriteResponses> getFavorites(@LoginMember Member member) {
        List<Favorite> favorites = favoriteService.getFavorites(member.getId());

        return ResponseEntity.ok(FavoriteResponses.of(FavoriteResponse.listOf(favorites)));
    }

    @DeleteMapping("/favorite/me/{id}")
    public ResponseEntity<Void> deleteFavorite(@LoginMember Member member, @PathVariable Long id) {
        favoriteService.deleteFavorite(member.getId(), id);
        return ResponseEntity.ok().build();
    }
}
