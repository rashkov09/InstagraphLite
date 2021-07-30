package softuni.exam.instagraphlite.models.dtos.jsonDtos;

import com.google.gson.annotations.Expose;

import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PictureSeedDto {
    @Expose
    private String path;
    @Expose
    private Double size;

    public PictureSeedDto() {
    }

    @NotNull

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Min(400)
    @Max(60000)
    @NotNull
    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }
}
