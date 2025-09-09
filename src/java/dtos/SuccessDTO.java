package dtos;

public class SuccessDTO {

    private boolean isSuccess;

    public SuccessDTO(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
