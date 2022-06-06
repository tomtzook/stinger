package stinger.storage.model;

import com.stinger.framework.commands.GenericCommandType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import stinger.storage.StandardProductType;

@Entity
@Table(name = "pros")
public class StoredProductModel {

    @Id
    @Column(name = "product_id", nullable = false)
    private String mProductId;
    @Column(name = "type", nullable = false)
    private int mType;
    @Column(name = "path", nullable = false)
    private String mPath;
    @Column(name = "metadata", nullable = true)
    private byte[] mMetadata;
    @Column(name = "is_commit", nullable = false)
    private Boolean mIsCommited;
    @Column(name = "priority", nullable = false)
    private Integer mPriority;

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public byte[] getMetadata() {
        return mMetadata;
    }

    public void setMetadata(byte[] metadata) {
        mMetadata = metadata;
    }

    public Boolean isCommited() {
        return mIsCommited;
    }

    public void setCommited(Boolean isCommited) {
        mIsCommited = isCommited;
    }

    public Integer getPriority() {
        return mPriority;
    }

    public void setPriority(Integer priority) {
        mPriority = priority;
    }
}
