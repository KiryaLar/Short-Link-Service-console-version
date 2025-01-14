package ru.larkin.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import ru.larkin.Config;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class Link {

    private String originalUrl;
    private UUID userUuid;
    private String shortLink;
    private LocalDateTime creationTime;
    private LocalDateTime expirationTime;

    @Builder.Default
    private int clickLimit = Config.CLICK_LIMIT;
    @Builder.Default
    private int clickCount = 0;
    @Builder.Default
    boolean isActive = true;

    public void incrementClickCount() {
        clickCount++;
        if (clickCount >= clickLimit) {
            isActive = false;
        }
    }

    public void setClickLimit(int newLimit) {
        if(newLimit <= 0) {
            throw new IllegalArgumentException("Click limit must be greater than 0");
        }
        this.clickLimit = newLimit;

        if (clickCount >= clickLimit) {
            this.isActive = false;
        } else {
            this.isActive = true;
        }
    }

    public void deactivate() {
        isActive = false;
    }
}