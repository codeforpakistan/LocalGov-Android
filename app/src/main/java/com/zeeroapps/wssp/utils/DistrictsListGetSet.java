package com.zeeroapps.wssp.utils;

/**
 * Created by Bilal on 18-Sep-18.
 */

public class DistrictsListGetSet {

    String id,
            districts_categories,
            level,
            parent_id,
            db_version,
            created_at,
            update_at,
            slug;


    public DistrictsListGetSet(String id, String districts_categories, String level,
                               String parent_id, String db_version, String created_at,
                               String update_at, String Slug) {
        this.id = id;
        this.districts_categories = districts_categories;
        this.level = level;
        this.parent_id = parent_id;
        this.db_version = db_version;
        this.created_at = created_at;
        this.update_at = update_at;
        this.slug  = Slug;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistricts_categories() {
        return districts_categories;
    }

    public void setDistricts_categories(String districts_categories) {
        this.districts_categories = districts_categories;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getDb_version() {
        return db_version;
    }

    public void setDb_version(String db_version) {
        this.db_version = db_version;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
