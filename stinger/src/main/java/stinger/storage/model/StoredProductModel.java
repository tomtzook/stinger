package stinger.storage.model;

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
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    private StandardProductType mType;
    @Column(name = "path", nullable = false)
    private String mPath;
    @Column(name = "metadata", nullable = true)
    private byte[] mMetadata;
    @Column(name = "is_commit", nullable = false)
    private Boolean mIsCommited;

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setType(StandardProductType type) {
        mType = type;
    }

    public StandardProductType getType() {
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
}
