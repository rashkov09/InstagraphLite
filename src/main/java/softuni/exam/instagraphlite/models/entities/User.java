package softuni.exam.instagraphlite.models.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String password;
    private Picture picture;
    private Set<Post> posts;

    @Column(nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToOne
    @NotNull
    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("User: %s\n" +
                "Post count: %d\n" +
                "==Post Details:\n", getUsername(), getPosts().size()));
        getPosts().forEach(post -> {
            builder.append(String.format("" +
                    "----Caption: %s\n" +
                    "----Picture Size: %.2f\n", post.getCaption(), post.getPicture().getSize()));
        });
        return builder.toString();
    }
}
