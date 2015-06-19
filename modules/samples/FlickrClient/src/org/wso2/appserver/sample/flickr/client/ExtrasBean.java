/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.appserver.sample.flickr.client;

public class ExtrasBean {

    private boolean license;

    private boolean date_upload;

    private boolean date_taken;

    private boolean owner_name;

    private boolean icon_server;

    private boolean original_format;

    private boolean last_update;

    private boolean geo;

    private boolean tags;

    private boolean machine_tags;


    public boolean isLicense() {
        return license;
    }

    public void setLicense(boolean license) {
        this.license = license;
    }

    public boolean isDate_upload() {
        return date_upload;
    }

    public void setDate_upload(boolean date_upload) {
        this.date_upload = date_upload;
    }

    public boolean isDate_taken() {
        return date_taken;
    }

    public void setDate_taken(boolean date_taken) {
        this.date_taken = date_taken;
    }

    public boolean isOwner_name() {
        return owner_name;
    }

    public void setOwner_name(boolean owner_name) {
        this.owner_name = owner_name;
    }

    public boolean isIcon_server() {
        return icon_server;
    }

    public void setIcon_server(boolean icon_server) {
        this.icon_server = icon_server;
    }

    public boolean isOriginal_format() {
        return original_format;
    }

    public void setOriginal_format(boolean original_format) {
        this.original_format = original_format;
    }

    public boolean isLast_update() {
        return last_update;
    }

    public void setLast_update(boolean last_update) {
        this.last_update = last_update;
    }

    public boolean isGeo() {
        return geo;
    }

    public void setGeo(boolean geo) {
        this.geo = geo;
    }

    public boolean isTags() {
        return tags;
    }

    public void setTags(boolean tags) {
        this.tags = tags;
    }

    public boolean isMachine_tags() {
        return machine_tags;
    }

    public void setMachine_tags(boolean machine_tags) {
        this.machine_tags = machine_tags;
    }
}
