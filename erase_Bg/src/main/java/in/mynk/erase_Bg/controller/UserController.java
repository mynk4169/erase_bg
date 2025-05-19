package in.mynk.erase_Bg.controller;

import in.mynk.erase_Bg.entity.User;
import in.mynk.erase_Bg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(
            user.getClerkId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        ));
    }

    @GetMapping("/{clerkId}")
    public ResponseEntity<User> getUserByClerkId(@PathVariable String clerkId) {
        return userService.findByClerkId(clerkId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{clerkId}")
    public ResponseEntity<User> updateUser(@PathVariable String clerkId, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(
            clerkId,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        ));
    }

    @DeleteMapping("/{clerkId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String clerkId) {
        userService.deactivateUser(clerkId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{clerkId}/credits")
    public ResponseEntity<User> updateCredits(@PathVariable String clerkId, @RequestParam int credits) {
        return ResponseEntity.ok(userService.updateCredits(clerkId, credits));
    }

    @PutMapping("/{clerkId}/plan")
    public ResponseEntity<User> updatePlan(@PathVariable String clerkId, @RequestParam String plan) {
        return ResponseEntity.ok(userService.updatePlan(clerkId, plan));
    }
}
