package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("{postId}")
    public Optional<Post> findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int from
    ) {
        SortOrder sortOrder = SortOrder.from(sort);

        if (sortOrder == null) {
            throw new ParameterNotValidException("Invalid sort order: %s".formatted(sort),
                    "sort");
        }

        if (size <= 0) {
            throw new ParameterNotValidException("Size should positive", "size");
        }

        if (from < 0) {
            throw new ParameterNotValidException("From should be greater than 0", "from");
        }

        return postService.findAll(sortOrder, size, from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}