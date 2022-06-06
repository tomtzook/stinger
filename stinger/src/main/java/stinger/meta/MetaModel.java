package stinger.meta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tool_meta")
public class MetaModel {

    @Id
    @Column(name = "tool_id", nullable = false)
    private String mId;
    @Column(name = "version", nullable = false)
    private String mVersion;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }
}
