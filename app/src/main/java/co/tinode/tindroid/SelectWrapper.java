package co.tinode.tindroid;

public class SelectWrapper<T> {
    private boolean isSelected;
    private  T data;

    public SelectWrapper(T data, boolean isSelected) {
        this.data = data;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelect) {
        this.isSelected = isSelect;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
