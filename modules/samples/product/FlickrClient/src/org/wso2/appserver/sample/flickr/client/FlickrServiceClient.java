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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.Token;
import org.apache.commons.codec.digest.DigestUtils;
import org.wso2.www.types.flickr.client.ActivityUserComments;
import org.wso2.www.types.flickr.client.ActivityUserPhotos;
import org.wso2.www.types.flickr.client.AnyOrAll;
import org.wso2.www.types.flickr.client.AuthGetFrob;
import org.wso2.www.types.flickr.client.AuthGetToken;
import org.wso2.www.types.flickr.client.AuthenticatedFlickrRequest;
import org.wso2.www.types.flickr.client.BlogsGetList;
import org.wso2.www.types.flickr.client.BlogsPostPhoto;
import org.wso2.www.types.flickr.client.ContactsGetList;
import org.wso2.www.types.flickr.client.ContactsGetPublicList;
import org.wso2.www.types.flickr.client.Err_type0;
import org.wso2.www.types.flickr.client.FavoritesAdd;
import org.wso2.www.types.flickr.client.FavoritesGetList;
import org.wso2.www.types.flickr.client.FavoritesGetPublicList;
import org.wso2.www.types.flickr.client.FavoritesRemove;
import org.wso2.www.types.flickr.client.Filter;
import org.wso2.www.types.flickr.client.GroupsBrowse;
import org.wso2.www.types.flickr.client.GroupsGetInfo;
import org.wso2.www.types.flickr.client.GroupsPoolsAdd;
import org.wso2.www.types.flickr.client.GroupsPoolsGetContext;
import org.wso2.www.types.flickr.client.GroupsPoolsGetGroups;
import org.wso2.www.types.flickr.client.GroupsSearch;
import org.wso2.www.types.flickr.client.PeopleFindByEmail;
import org.wso2.www.types.flickr.client.PeopleFindByUsername;
import org.wso2.www.types.flickr.client.PeopleGetInfo;
import org.wso2.www.types.flickr.client.PeopleGetPublicGroups;
import org.wso2.www.types.flickr.client.PeopleGetPublicPhotos;
import org.wso2.www.types.flickr.client.PeopleGetUploadStatus;
import org.wso2.www.types.flickr.client.PhotosAddTags;
import org.wso2.www.types.flickr.client.PhotosDelete;
import org.wso2.www.types.flickr.client.PhotosGeoGetLocation;
import org.wso2.www.types.flickr.client.PhotosGeoGetPerms;
import org.wso2.www.types.flickr.client.PhotosGeoRemoveLocation;
import org.wso2.www.types.flickr.client.PhotosGeoSetLocation;
import org.wso2.www.types.flickr.client.PhotosGeoSetPerms;
import org.wso2.www.types.flickr.client.PhotosGetAllContexts;
import org.wso2.www.types.flickr.client.PhotosGetContactsPhotos;
import org.wso2.www.types.flickr.client.PhotosGetContactsPublicPhotos;
import org.wso2.www.types.flickr.client.PhotosGetContext;
import org.wso2.www.types.flickr.client.PhotosGetCounts;
import org.wso2.www.types.flickr.client.PhotosGetExif;
import org.wso2.www.types.flickr.client.PhotosGetFavorites;
import org.wso2.www.types.flickr.client.PhotosGetInfo;
import org.wso2.www.types.flickr.client.PhotosGetNotInSet;
import org.wso2.www.types.flickr.client.PhotosGetPerms;
import org.wso2.www.types.flickr.client.PhotosGetRecent;
import org.wso2.www.types.flickr.client.PhotosGetSizes;
import org.wso2.www.types.flickr.client.PhotosGetUntagged;
import org.wso2.www.types.flickr.client.PhotosGetWithGeoData;
import org.wso2.www.types.flickr.client.PhotosGetWithoutGeoData;
import org.wso2.www.types.flickr.client.PhotosRecentlyUpdated;
import org.wso2.www.types.flickr.client.PhotosRemoveTag;
import org.wso2.www.types.flickr.client.PhotosSearch;
import org.wso2.www.types.flickr.client.PhotosSetDates;
import org.wso2.www.types.flickr.client.PhotosSetMeta;
import org.wso2.www.types.flickr.client.PhotosSetPerms;
import org.wso2.www.types.flickr.client.PhotosSetTags;
import org.wso2.www.types.flickr.client.PhotosetsCommentsAddComment;
import org.wso2.www.types.flickr.client.PhotosetsCommentsDeleteComment;
import org.wso2.www.types.flickr.client.PhotosetsCommentsEditComment;
import org.wso2.www.types.flickr.client.PhotosetsCommentsGetList;
import org.wso2.www.types.flickr.client.Rsp;
import org.wso2.www.types.flickr.client.SignedFlickrRequest;
import org.wso2.www.types.flickr.client.SortOrder;
import org.wso2.www.types.flickr.client.StatType;
import org.wso2.www.types.flickr.client.UnixTimeStamp;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Iterator;

public class FlickrServiceClient {

    public String flickrPeopleFindByEmail(String email, String key, String host, String port) {

        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PeopleFindByEmail peopleFindByEmail = new PeopleFindByEmail();
        peopleFindByEmail.setApi_key(key);
        peopleFindByEmail.setFind_email(email);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPeopleFindByEmail(peopleFindByEmail);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    output = "The Details of the following user was retrived\n\n";
                    output = output + "Username : " + user.getFirstElement().getText() + "\n";
                    output = output + "User ID : " + user.getAttributeValue(new QName("id")) + "\n";
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPeopleFindByUsername(String name, String key, String host,
                                                 String port) {

        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PeopleFindByUsername peopleFindByUsername = new PeopleFindByUsername();
        peopleFindByUsername.setApi_key(key);
        peopleFindByUsername.setUsername(name);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPeopleFindByUsername(peopleFindByUsername);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    OMAttribute omAttribute = user.getAttribute(new QName("id"));
                    output = "The Details of the following user was retrived\n\n";
                    output = output + "Username : " + user.getFirstElement().getText() + "\n";
                    output = output + user.getLocalName() + " " + omAttribute.getLocalName() + " : " +
                            omAttribute.getAttributeValue();
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    private String processPeopleError(Err_type0 error) {
        String output;
        output = "An error occuerd, while invoking the service.\n\n";
        String code = error.getCode();
        if ("1".equals(code)) {
            output = output + "The user id passed did not match a Flickr user.";
        } else if ("100".equals(code)) {
            output = output + "The API key passed was not valid or has expired.";
        } else if ("105".equals(code)) {
            output = output + "The requested service is temporarily unavailable.";
        } else if ("111".equals(code)) {
            output = output + "The requested response format was not found.";
        } else if ("112".equals(code)) {
            output = output + "The requested method was not found.";
        }
        return output;
    }

    private String processPhotosError(Err_type0 error) {
        String output;
        output = "An error occuerd, while invoking the service.\n\n";
        String code = error.getCode();
        if ("1".equals(code)) {
            output = output +
                    "The id passed could be an invalid id, or the user may not have permission to manipulate it.";
        } else if ("2".equals(code)) {
            output = output +
                    "The maximum number of tags for the photo has been reached - no more tags can be added. If the current count is less than the maximum, but adding all of the tags for this request would go over the limit, the whole request is ignored. I.E. when you get this message, none of the requested tags have been added.";
        } else if ("96".equals(code)) {
            output = output + "The passed signature was invalid.";
        } else if ("97".equals(code)) {
            output = output + "The call required signing but no signature was sent.";
        } else if ("98".equals(code)) {
            output = output + "The login details or auth token passed were invalid.";
        } else if ("99".equals(code)) {
            output = output +
                    "The method requires user authentication but the user was not logged in, or the authenticated method call did not have the required permissions.";
        } else if ("100".equals(code)) {
            output = output + "The API key passed was not valid or has expired.";
        } else if ("105".equals(code)) {
            output = output + "The requested service is temporarily unavailable.";
        } else if ("111".equals(code)) {
            output = output + "The requested response format was not found.";
        } else if ("112".equals(code)) {
            output = output + "The requested method was not found.";
        }
        return output;
    }

    public String flickrPeopleGetInfo(String user_ID, String key, String host, String port) {

        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PeopleGetInfo peopleGetInfo = new PeopleGetInfo();
        peopleGetInfo.setApi_key(key);
        peopleGetInfo.setUser_id(user_ID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPeopleGetInfo(peopleGetInfo);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    output = "The Information of the following user was retrived\n\n";
                    OMAttribute omAttribute = user.getAttribute(new QName("id"));
                    output = output + "The ID of the person is : " +
                            omAttribute.getAttributeValue() + "\n";
                    OMElement username = user.getFirstChildWithName(new QName("username"));
                    output = output + "The name of the person is : " + username.getText() + "\n";
                    OMElement realname = user.getFirstChildWithName(new QName("realname"));
                    output = output + "The real name of the person is : " + realname.getText() +
                            "\n";
                    OMElement location = user.getFirstChildWithName(new QName("location"));
                    output =
                            output + "The location of the person is : " + location.getText() + "\n";
                    OMElement photosurl = user.getFirstChildWithName(new QName("photosurl"));
                    output = output + "The photosurl is : " + photosurl.getText() + "\n";
                    OMElement profileurl = user.getFirstChildWithName(new QName("profileurl"));
                    output = output + "The profileurl is : " + profileurl.getText() + "\n\n";
                    output = output + "Details of photos taken\n\n";
                    OMElement photos = user.getFirstChildWithName(new QName("photos"));
                    OMElement firstdate = photos.getFirstChildWithName(new QName("firstdate"));
                    output =
                            output + "The First photo was taken on : " + firstdate.getText() + "\n";
                    OMElement firstdatetaken =
                            photos.getFirstChildWithName(new QName("firstdatetaken"));
                    output = output + "The firstdatetaken was taken on : " +
                            firstdatetaken.getText() + "\n";
                    OMElement count = photos.getFirstChildWithName(new QName("count"));
                    output = output + "The number of photos : " + count.getText();
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPeopleGetPublicGroups(String user_ID, String key, String host,
                                                  String port) {

        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PeopleGetPublicGroups peopleGetPublicGroups = new PeopleGetPublicGroups();
        peopleGetPublicGroups.setApi_key(key);
        peopleGetPublicGroups.setUser_id(user_ID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPeopleGetPublicGroups(peopleGetPublicGroups);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    output = "Public groups of the user\n\n";
                    Iterator iterator = user.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement childElement = (OMElement) iterator.next();
                        output = output + "Group ID : " +
                                childElement.getAttribute(new QName("nsid")).getAttributeValue() +
                                "\nGroup name : " +
                                childElement.getAttribute(new QName("name")).getAttributeValue() +
                                "\n\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPeopleGetPublicPhotos(String user_ID, String key, String host,
                                                  String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PeopleGetPublicPhotos peopleGetPublicPhotos = new PeopleGetPublicPhotos();
        peopleGetPublicPhotos.setApi_key(key);
        peopleGetPublicPhotos.setUser_id(user_ID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPeopleGetPublicPhotos(peopleGetPublicPhotos);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    output = "Public photos of the user\n\n";
                    Iterator iterator = user.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement childElement = (OMElement) iterator.next();
                        output = output + "Photo ID : " +
                                childElement.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                childElement.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n" + "Title : " +
                                childElement.getAttribute(new QName("title")).getAttributeValue() +
                                "\n\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPeopleGetUploadStatus(String sharedSecret, String token, String key,
                                                  String host,
                                                  String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PeopleGetUploadStatus peopleGetUploadStatus = new PeopleGetUploadStatus();
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.people.getUploadStatus";
        peopleGetUploadStatus.setApi_key(key);
        peopleGetUploadStatus.setAuth_token(token);
        String output = "";
        try {
            peopleGetUploadStatus.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPeopleGetUploadStatus(peopleGetUploadStatus);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    output = "Upload status of the user\n\n";
                    output = output + "User ID : " +
                            user.getAttribute(new QName("id")).getAttributeValue() + "\n";
                    OMElement usernameElement = user.getFirstChildWithName(new QName("username"));
                    output = output + "The name of the person is : " + usernameElement.getText() +
                            "\n";
                    OMElement bandwidth = user.getFirstChildWithName(new QName("bandwidth"));
                    output = output + "Bandwidth information\n";
                    output = output + "Maximum Bytes : " +
                            bandwidth.getAttribute(new QName("maxbytes")).getAttributeValue() +
                            "\n";
                    output = output + "Maximum KiloBytes : " +
                            bandwidth.getAttribute(new QName("maxkb")).getAttributeValue() + "\n";
                    output = output + "Used Bytes : " +
                            bandwidth.getAttribute(new QName("usedbytes")).getAttributeValue() +
                            "\n";
                    output = output + "Used KiloBytes : " +
                            bandwidth.getAttribute(new QName("usedkb")).getAttributeValue() + "\n";
                    output = output + "Remaining Bytes : " +
                            bandwidth.getAttribute(new QName("remainingbytes"))
                                    .getAttributeValue() + "\n";
                    output = output + "Remaining KiloBytes : " +
                            bandwidth.getAttribute(new QName("remainingkb")).getAttributeValue() +
                            "\n";
                    OMElement filesize = user.getFirstChildWithName(new QName("filesize"));
                    output = output + "FileSize information\n";
                    output = output + "Maximum Bytes : " +
                            filesize.getAttribute(new QName("maxbytes")).getAttributeValue() + "\n";
                    output = output + "Maximum KiloBytes : " +
                            filesize.getAttribute(new QName("maxkb")).getAttributeValue() + "\n";
                    OMElement sets = user.getFirstChildWithName(new QName("sets"));
                    output = output + "Information on sets\n";
                    output = output + "Created : " +
                            sets.getAttribute(new QName("created")).getAttributeValue() + "\n";
                    output = output + "Remaining : " +
                            sets.getAttribute(new QName("remaining")).getAttributeValue() + "\n";
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosSearch(String user_ID, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosSearch photosSearch = new PhotosSearch();
        photosSearch.setApi_key(key);
        photosSearch.setUser_id(user_ID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosSearch(photosSearch);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement user = elements[i];
                    output = "Public photos of the user\n\n";
                    Iterator iterator = user.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement childElement = (OMElement) iterator.next();
                        output = output + "Photo ID : " +
                                childElement.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                childElement.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n" + "Title : " +
                                childElement.getAttribute(new QName("title")).getAttributeValue() +
                                "\n\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

     public String flickrAuthGetFrob(String key, String host, String port, String sharedSecret)
            throws Exception {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        AuthGetFrob authGetFrob = new AuthGetFrob();
        SignedFlickrRequest signedFlickrRequest = new SignedFlickrRequest();
        signedFlickrRequest.setApi_key(key);

        String output = "";
        String sigString = sharedSecret + "api_key" + key + "formatrestmethodflickr.auth.getFrob";
        String sig = DigestUtils.md5Hex(sigString);
        signedFlickrRequest.setApi_sig(sig);
        authGetFrob.setAuthGetFrob(signedFlickrRequest);
        Rsp response = flickrServiceStub.flickrAuthGetFrob(authGetFrob);
        StatType statType = response.getStat();
        Token responseToken = statType.getValue();
        if (responseToken.equals(StatType._ok)) {
            OMElement[] elements = response.getExtraElement();
            for (int i = 0; i < elements.length; i++) {
                OMElement frob = elements[i];
                output = frob.getText();
            }
        } else {
            throw new Exception(processPhotosError(response.getErr()));
        }
        return output;
    }

    public String flickrAuthGetToken(String key, String host, String port, String sharedSecret,
                                     String frob)
            throws Exception {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        AuthGetToken authGetToken = new AuthGetToken();
        SignedFlickrRequest signedFlickrRequest = new SignedFlickrRequest();
        signedFlickrRequest.setApi_key(key);

        String output = "";
        String sigString = sharedSecret + "api_key" + key + "formatrestfrob" + frob +
                "methodflickr.auth.getToken";
        String sig = DigestUtils.md5Hex(sigString);
        authGetToken.setApi_key(key);
        authGetToken.setApi_sig(sig);
        authGetToken.setFrob(frob);
        Rsp response = flickrServiceStub.flickrAuthGetToken(authGetToken);
        StatType statType = response.getStat();
        Token responseToken = statType.getValue();
        if (responseToken.equals(StatType._ok)) {
            OMElement[] elements = response.getExtraElement();
            for (int i = 0; i < elements.length; i++) {
                OMElement auth = elements[i];
                OMElement token = auth.getFirstChildWithName(new QName("token"));
                output = token.getText();
            }
        } else {
            throw new Exception(processPhotosError(response.getErr()));
        }
        return output;
    }


    public String flickrActivityUserComments(String page, String perPage, String sharedSecret,
                                             String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.activity.userComments";
        ActivityUserComments activityUserComments = new ActivityUserComments();
        activityUserComments.setApi_key(key);
        activityUserComments.setAuth_token(token);
        if (!"".equals(page.trim())) {
            activityUserComments.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            activityUserComments.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
        String output = "";
        try {
            activityUserComments.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrActivityUserComments(activityUserComments);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement items = elements[i];
                    output = "Activity on photos commented by calling user\n\n";
                    Iterator iter = items.getChildElements();
                    while (iter.hasNext()) {
                        OMElement item = (OMElement) iter.next();
                        output = output + "Item \nItem ID : " +
                                item.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                item.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n";
                        output = output + "Item Secret : " +
                                item.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n";
                        OMElement title = item.getFirstChildWithName(new QName("title"));
                        output = output + "Title : " + title.getText();
                        OMElement activity = item.getFirstChildWithName(new QName("activity"));
                        Iterator iter2 = activity.getChildElements();
                        while (iter2.hasNext()) {
                            OMElement event = (OMElement) iter2.next();
                            output = output + "Activity \nUser ID : " +
                                    event.getAttribute(new QName("user")).getAttributeValue() +
                                    "\nUsername : " +
                                    event.getAttribute(new QName("username")).getAttributeValue() +
                                    "\nDate Added : " +
                                    event.getAttribute(new QName("dateadded")).getAttributeValue() +
                                    "\n"  + "Text : " + event.getText();
                        }

                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrActivityUserPhotos(String page, String perPage, String timeFrame,
                                           String sharedSecret, String token, String key,
                                           String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.activity.userPhotos";
        ActivityUserPhotos activityUserPhotos = new ActivityUserPhotos();
        activityUserPhotos.setApi_key(key);
        activityUserPhotos.setAuth_token(token);
        if (!"".equals(page.trim())) {
            activityUserPhotos.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            activityUserPhotos.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
        if (!"".equals(timeFrame.trim())) {
            activityUserPhotos.setTimeframe(timeFrame);
            sig = sig + "timeframe" + timeFrame;
        }
        String output = "";
        try {
            activityUserPhotos.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrActivityUserPhotos(activityUserPhotos);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement items = elements[i];
                    output = "Activity on photos belonging to the calling user\n\n";
                    Iterator iter = items.getChildElements();
                    while (iter.hasNext()) {
                        OMElement item = (OMElement) iter.next();
                        output = output + "Item \nItem ID : " +
                                item.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                item.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n";
                        output = output + "Item Secret : " +
                                item.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n";
                        output = output + "Old Comments : " +
                                item.getAttribute(new QName("commentsold")).getAttributeValue() +
                                "\n";
                        output = output + "New Comments : " +
                                item.getAttribute(new QName("commentsnew")).getAttributeValue() +
                                "\n";
                        output = output + "Views : " +
                                item.getAttribute(new QName("views")).getAttributeValue() +
                                "\n";
                        OMElement title = item.getFirstChildWithName(new QName("title"));
                        output = output + "Title : " + title.getText();
                        OMElement activity = item.getFirstChildWithName(new QName("activity"));
                        Iterator iter2 = activity.getChildElements();
                        while (iter2.hasNext()) {
                            OMElement event = (OMElement) iter2.next();
                            output = output + "Activity \nUser ID : " +
                                    event.getAttribute(new QName("user")).getAttributeValue() +
                                    "\nUsername : " +
                                    event.getAttribute(new QName("username")).getAttributeValue() +
                                    "\nDate Added : " +
                                    event.getAttribute(new QName("dateadded")).getAttributeValue() +
                                    "\n" + "Text : " + event.getText();
                        }

                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrBlogsGetList(String sharedSecret, String token, String key, String host,
                                     String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.blogs.getList";
        BlogsGetList blogsGetList = new BlogsGetList();
        AuthenticatedFlickrRequest authenticatedFlickrRequest = new AuthenticatedFlickrRequest();
        authenticatedFlickrRequest.setApi_key(key);
        authenticatedFlickrRequest.setAuth_token(token);
        String output = "";
        try {
            authenticatedFlickrRequest.setApi_sig(DigestUtils.md5Hex(sig));
            blogsGetList.setBlogsGetList(authenticatedFlickrRequest);
            Rsp response = flickrServiceStub.flickrBlogsGetList(blogsGetList);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement blogs = elements[i];
                    output = "List of configured blogs for the calling user\n\n";
                    Iterator iter = blogs.getChildElements();
                    while (iter.hasNext()) {
                        OMElement blog = (OMElement) iter.next();
                        output = output + "Blog \nBlog ID : " +
                                blog.getAttribute(new QName("id")).getAttributeValue() +
                                "\nName : " +
                                blog.getAttribute(new QName("name")).getAttributeValue() +
                                "\n" + "URL : " +
                                blog.getAttribute(new QName("url")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrBlogsPostPhoto(String blogID, String photoID, String title,
                                       String description, String password, String sharedSecret,
                                       String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "blog_id" + blogID;
        BlogsPostPhoto blogsPostPhoto = new BlogsPostPhoto();
        if ("".equals(password.trim())) {
            blogsPostPhoto.setBlog_password(password);
            sig = sig + "blog_password" + password;
        }
        sig = sig + "description" + description + "formatrest" + "method" +
                "flickr.blogs.postPhoto" + "photo_id" + photoID + "title" + title;
        blogsPostPhoto.setApi_key(key);
        blogsPostPhoto.setAuth_token(token);
        blogsPostPhoto.setBlog_id(blogID);
        blogsPostPhoto.setPhoto_id(photoID);
        blogsPostPhoto.setTitle(title);
        blogsPostPhoto.setDescription(description);
        String output;
        try {
            blogsPostPhoto.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrBlogsPostPhoto(blogsPostPhoto);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Photo ID added succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosetsCommentsAddComment(String comment, String photoID,
                                                    String sharedSecret, String token, String key,
                                                    String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "comment_text" +
                comment + "formatrest" + "method" + "flickr.photosets.comments.addComment" +
                "photoset_id" + photoID;
        PhotosetsCommentsAddComment photosetsCommentsAddComment = new PhotosetsCommentsAddComment();
        photosetsCommentsAddComment.setApi_key(key);
        photosetsCommentsAddComment.setAuth_token(token);
        photosetsCommentsAddComment.setComment_text(comment);
        photosetsCommentsAddComment.setPhotoset_id(photoID);
        String output = "";
        try {
            photosetsCommentsAddComment.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub
                    .flickrPhotosetsCommentsAddComment(photosetsCommentsAddComment);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement commentEle = elements[i];
                    output = "Comment added succesfully\n\n" + "comment ID : " +
                            commentEle.getAttribute(new QName("id")).getAttributeValue();
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosetsCommentsDeleteComment(String commentID, String sharedSecret,
                                                       String token, String key, String host,
                                                       String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "comment_id" +
                commentID + "formatrest" + "method" + "flickr.photosets.comments.deleteComment";
        PhotosetsCommentsDeleteComment photosetsCommentsDeleteComment =
                new PhotosetsCommentsDeleteComment();
        photosetsCommentsDeleteComment.setApi_key(key);
        photosetsCommentsDeleteComment.setAuth_token(token);
        photosetsCommentsDeleteComment.setComment_id(commentID);
        String output = "";
        try {
            photosetsCommentsDeleteComment.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub
                    .flickrPhotosetsCommentsDeleteComment(photosetsCommentsDeleteComment);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Comment deleted succesfully\n\n";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosetsCommentsEditComment(String comment, String commentID,
                                                     String sharedSecret, String token, String key,
                                                     String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "comment_id" +
                commentID + "comment_text" + comment + "formatrest" + "method" +
                "flickr.photosets.comments.editComment";
        PhotosetsCommentsEditComment photosetsCommentsEditComment =
                new PhotosetsCommentsEditComment();
        photosetsCommentsEditComment.setApi_key(key);
        photosetsCommentsEditComment.setAuth_token(token);
        photosetsCommentsEditComment.setComment_text(comment);
        photosetsCommentsEditComment.setComment_id(commentID);
        String output = "";
        try {
            photosetsCommentsEditComment.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub
                    .flickrPhotosetsCommentsEditComment(photosetsCommentsEditComment);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Comment edited succesfully\n\n";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosetsCommentsGetList(String photoID, String sharedSecret, String token,
                                                 String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosetsCommentsGetList photosetsCommentsGetList = new PhotosetsCommentsGetList();
        photosetsCommentsGetList.setApi_key(key);
        photosetsCommentsGetList.setPhotoset_id(photoID);
        String output = "";
        try {
            Rsp response =
                    flickrServiceStub.flickrPhotosetsCommentsGetList(photosetsCommentsGetList);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement commentsEle = elements[i];
                    output = "Comment for the specified Photoset\n\n";
                    output = output + "Photoset ID : " +
                            commentsEle.getAttribute(new QName("id")).getAttributeValue() + "\n";
                    Iterator iter = commentsEle.getChildElements();
                    while (iter.hasNext()) {
                        OMElement commentEle = (OMElement) iter.next();
                        output = output + "Comment ID : " +
                                commentEle.getAttribute(new QName("id")).getAttributeValue() + "\n";
                        output = output + "Author ID : " +
                                commentEle.getAttribute(new QName("author")).getAttributeValue() +
                                "\n";
                        output = output + "Author Name : " + commentEle
                                .getAttribute(new QName("authorname")).getAttributeValue() + "\n";
                        output = output + "Date Created : " + commentEle
                                .getAttribute(new QName("date_create")).getAttributeValue() + "\n";
                        output = output + "Permanant Link : " + commentEle
                                .getAttribute(new QName("permalink")).getAttributeValue() + "\n";
                        output = output + "Comment : " + commentEle.getText();
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrContactsGetList(Object filterValue, String page, String perPage,
                                        String sharedSecret, String token, String key, String host,
                                        String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token;
        ContactsGetList contactsGetList = new ContactsGetList();
        contactsGetList.setApi_key(key);
        contactsGetList.setAuth_token(token);
        if (filterValue instanceof Filter) {
            contactsGetList.setFilter((Filter) filterValue);
            sig = sig + "filter" + filterValue.toString();
        }
        sig = sig + "formatrest" + "method" + "flickr.contacts.getList";
        if (!"".equals(page.trim())) {
            contactsGetList.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            contactsGetList.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
        String output = "";
        try {
            contactsGetList.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrContactsGetList(contactsGetList);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement contacts = elements[i];
                    output = "Contacts of the calling user\n\n";
                    output = output + "Total number of contacts : " +
                            contacts.getAttribute(new QName("total")).getAttributeValue() + "\n";
                    Iterator iter = contacts.getChildElements();
                    while (iter.hasNext()) {
                        OMElement contact = (OMElement) iter.next();
                        output = output + "Contact\n";
                        output = output + "Contact ID : " +
                                contact.getAttribute(new QName("nsid")).getAttributeValue() + "\n";
                        output = output + "Username : " +
                                contact.getAttribute(new QName("username")).getAttributeValue() +
                                "\n";
                        output = output + "Real Name : " +
                                contact.getAttribute(new QName("realname")).getAttributeValue() +
                                "\n";
                        output = output + "Friend : " +
                                contact.getAttribute(new QName("friend")).getAttributeValue() +
                                "\n";
                        output = output + "Family : " +
                                contact.getAttribute(new QName("family")).getAttributeValue() +
                                "\n";
                        output = output + "Ignored : " +
                                contact.getAttribute(new QName("ignored")).getAttributeValue() + "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrContactsGetPublicList(String userID, String page, String perPage,
                                              String sharedSecret, String token, String key,
                                              String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        ContactsGetPublicList contactsGetPublicList = new ContactsGetPublicList();
        contactsGetPublicList.setApi_key(key);
        contactsGetPublicList.setUser_id(userID);
        if (!"".equals(page.trim())) {
            contactsGetPublicList.setPage(new BigInteger(page));
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            contactsGetPublicList.setPer_page(new BigInteger(perPage));
        }
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrContactsGetPublicList(contactsGetPublicList);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement contacts = elements[i];
                    output = "Contacts list for a user\n\n";
                    output = output + "Total number of contacts : " +
                            contacts.getAttribute(new QName("total")).getAttributeValue() + "\n";
                    Iterator iter = contacts.getChildElements();
                    while (iter.hasNext()) {
                        OMElement contact = (OMElement) iter.next();
                        output = output + "Contact\n";
                        output = output + "Contact ID : " +
                                contact.getAttribute(new QName("nsid")).getAttributeValue() + "\n";
                        output = output + "Username : " +
                                contact.getAttribute(new QName("username")).getAttributeValue() +
                                "\n";
                        output = output + "Ignored : " +
                                contact.getAttribute(new QName("ignored")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrFavoritesAdd(String photoID, String sharedSecret, String token, String key,
                                     String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.favorites.add";
        FavoritesAdd favoritesAdd = new FavoritesAdd();
        favoritesAdd.setApi_key(key);
        favoritesAdd.setAuth_token(token);
        favoritesAdd.setPhoto_id(photoID);
        sig = sig + "photo_id" + photoID;
        String output = "";
        try {
            favoritesAdd.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrFavoritesAdd(favoritesAdd);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Added to favorites succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrFavoritesGetList(String userID, String page, String perPage,
                                         ExtrasBean extras, String sharedSecret, String token,
                                         String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token;
        FavoritesGetList favoritesGetList = new FavoritesGetList();
        favoritesGetList.setApi_key(key);
        favoritesGetList.setAuth_token(token);
        String extrasString = processExtras(extras);
        if (!"".equals(extrasString.trim())) {
            favoritesGetList.setExtras(extrasString);
            sig = sig + "extras" + extrasString;
        }
        sig = sig + "formatrest" + "method" + "flickr.favorites.getList";
        if (!"".equals(page.trim())) {
            favoritesGetList.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            favoritesGetList.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
        if (userID != null && !"".equals(userID.trim())) {
            favoritesGetList.setUser_id(userID);
            sig = sig + "user_id" + userID;
        }
        String output = "";
        try {
            favoritesGetList.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrFavoritesGetList(favoritesGetList);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "Favorite photos of the user\n\n";
                    Iterator iterator = photos.getChildElements();
                    output = output + "Total Photos : " +
                            photos.getAttribute(new QName("total")).getAttributeValue() + "\n";
                    while (iterator.hasNext()) {
                        output = output + "Photo\n";
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "Photo ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n"  + "Secret : " +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\n\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrFavoritesGetPublicList(String userID, String page, String perPage,
                                               ExtrasBean extras, String sharedSecret, String token,
                                               String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        FavoritesGetPublicList favoritesGetPublicList = new FavoritesGetPublicList();
        favoritesGetPublicList.setApi_key(key);
        String extrasString = processExtras(extras);
        if (!"".equals(extrasString.trim())) {
            favoritesGetPublicList.setExtras(extrasString);
        }
        if (!"".equals(page.trim())) {
            favoritesGetPublicList.setPage(new BigInteger(page));
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            favoritesGetPublicList.setPer_page(new BigInteger(perPage));
        }
        if (userID != null && !"".equals(userID.trim())) {
            favoritesGetPublicList.setUser_id(userID);
        }
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrFavoritesGetPublicList(favoritesGetPublicList);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "Favorite public photos for a given user\n\n";
                    Iterator iterator = photos.getChildElements();
                    output = output + "Total Photos : " +
                            photos.getAttribute(new QName("total")).getAttributeValue() + "\n";
                    while (iterator.hasNext()) {
                        output = output + "Photo\n";
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "Photo ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n" + "Secret : " +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\n\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrFavoritesRemove(String photoID, String sharedSecret, String token,
                                        String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        FavoritesRemove favoritesRemove = new FavoritesRemove();
        favoritesRemove.setApi_key(key);
        favoritesRemove.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrFavoritesRemove(favoritesRemove);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Removed from favorites succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGeoGetLocation(String photoID, String sharedSecret, String token,
                                             String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGeoGetLocation photosGeoGetLocation = new PhotosGeoGetLocation();
        photosGeoGetLocation.setApi_key(key);
        photosGeoGetLocation.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGeoGetLocation(photosGeoGetLocation);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photo = elements[i];
                    OMElement location = photo.getFirstChildWithName(new QName("location"));
                    output = "Geo data of the requested photo\n\n" + "Photo ID : " + photo.getAttribute(new QName("id")) +
                            "\n" + "Latitude : " + location.getAttribute(new QName("latitude")) +
                            "\n" + "Longitude : " +
                            location.getAttribute(new QName("longitude")) + "\n";
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGeoGetPerms(String photoID, String sharedSecret, String token,
                                          String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.photos.geo.getPerms";
        PhotosGeoGetPerms photosGeoGetPerms = new PhotosGeoGetPerms();
        photosGeoGetPerms.setApi_key(key);
        photosGeoGetPerms.setAuth_token(token);
        photosGeoGetPerms.setPhoto_id(photoID);
        sig = sig + "photo_id" + photoID;
        String output = "";
        try {
            photosGeoGetPerms.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGeoGetPerms(photosGeoGetPerms);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement perms = elements[i];
                    output = "Permissions for who may view geo data of this photo\n\n" + "Photo ID : " + perms.getAttribute(new QName("id")) +
                            "\n" + "Is Public : " + perms.getAttribute(new QName("ispublic")) +
                            "\n" + "Is Contact : " + perms.getAttribute(new QName("iscontact")) +
                            "\n" + "Is Friend : " + perms.getAttribute(new QName("isfriend")) +
                            "\n" + "Is Family : " + perms.getAttribute(new QName("isfamily")) +
                            "\n";
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGeoRemoveLocation(String photoID, String sharedSecret, String token,
                                                String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.photos.geo.removeLocation";
        PhotosGeoRemoveLocation photosGeoRemoveLocation = new PhotosGeoRemoveLocation();
        photosGeoRemoveLocation.setApi_key(key);
        photosGeoRemoveLocation.setPhoto_id(photoID);
        sig = sig + "photo_id" + photoID;
        String output = "";
        try {
            photosGeoRemoveLocation.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGeoRemoveLocation(photosGeoRemoveLocation);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Geo tags removed succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGeoSetLocation(String photoID, String lat, String lon,
                                             String accuracy, String sharedSecret, String token,
                                             String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key;
        PhotosGeoSetLocation photosGeoSetLocation = new PhotosGeoSetLocation();
        photosGeoSetLocation.setApi_key(key);
        photosGeoSetLocation.setAuth_token(token);
        if (accuracy != null && !"".equals(accuracy.trim())) {
            photosGeoSetLocation.setAccuracy(new BigInteger(accuracy));
            sig = sig + "accuracy" + accuracy;
        }
        sig = sig + "auth_token" + token + "formatrest";
        photosGeoSetLocation.setLat(lat);
        photosGeoSetLocation.setLon(lon);
        sig = sig + "lat" + lat + " lon" + lon + "method" + "flickr.photos.geo.setLocation";
        photosGeoSetLocation.setPhoto_id(photoID);
        sig = sig + "photo_id" + photoID;
        String output = "";
        try {
            photosGeoSetLocation.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGeoSetLocation(photosGeoSetLocation);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Geo tags added succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGeoSetPerms(String photoID, boolean isPublic, boolean isContact,
                                          boolean isFriend, boolean isFamily, String sharedSecret,
                                          String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest";
        PhotosGeoSetPerms photosGeoSetPerms = new PhotosGeoSetPerms();
        photosGeoSetPerms.setApi_key(key);
        photosGeoSetPerms.setAuth_token(token);
        photosGeoSetPerms.setPhoto_id(photoID);

        if (isContact) {
            photosGeoSetPerms.setIs_contact(new BigInteger("1"));
            sig = sig + "is_contact1";
        }
        if (isFamily) {
            photosGeoSetPerms.setIs_contact(new BigInteger("1"));
            sig = sig + "is_family1";
        }
        if (isFriend) {
            photosGeoSetPerms.setIs_contact(new BigInteger("1"));
            sig = sig + "is_friend1";
        }
        if (isPublic) {
            photosGeoSetPerms.setIs_contact(new BigInteger("1"));
            sig = sig + "is_public1";
        }
        sig = sig + "method" + "flickr.photos.geo.setPerms" + "photo_id" + photoID;
        String output = "";
        try {
            photosGeoSetPerms.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGeoSetPerms(photosGeoSetPerms);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Permissions set succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrGroupsBrowse(String catID, String sharedSecret, String token, String key,
                                     String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token;
        GroupsBrowse groupsBrowse = new GroupsBrowse();
        groupsBrowse.setApi_key(key);
        groupsBrowse.setAuth_token(token);
        if (catID != null && !"".equals(catID.trim())) {
            groupsBrowse.setCat_id(catID);
            sig = sig + "cat_id" + catID;
        }
        sig = sig + "formatrest" + "method" + "flickr.groups.browse";
        String output = "";
        try {
            groupsBrowse.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrGroupsBrowse(groupsBrowse);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement category = elements[i];
                    output = "Group Category tree details\n\n" + "Category Name : " +
                            category.getAttribute(new QName("name")) + "\n" + "Category Path : " +
                            category.getAttribute(new QName("path")) + "\n";
                    Iterator iterator = category.getChildrenWithName(new QName("subcat"));
                    while (iterator.hasNext()) {
                        OMElement subCats = (OMElement) iterator.next();
                        output = output + "Sub Category\n" + "Sub Category ID : " +
                                subCats.getAttribute(new QName("id")) + "\n" + "Sub Category Name : " +
                                subCats.getAttribute(new QName("name")) + "\n" + "Sub Category Count : " +
                                subCats.getAttribute(new QName("count")) + "\n";
                    }
                    Iterator iterator2 = category.getChildrenWithName(new QName("group"));
                    while (iterator2.hasNext()) {
                        OMElement group = (OMElement) iterator2.next();
                        output = output + "Group\n" + "Group ID : " + group.getAttribute(new QName("id")) + "\n" +
                                "Group Name : " + group.getAttribute(new QName("name")) +
                                "\n" + "Number of members : " +
                                group.getAttribute(new QName("members")) + "\n" + "Number of members currently online : " +
                                group.getAttribute(new QName("online")) + "\n" + "Number of people in groups chat : " +
                                group.getAttribute(new QName("inchat")) + "\n";
                    }

                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrGroupsGetInfo(String groupID, String sharedSecret, String token, String key,
                                      String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        GroupsGetInfo groupsGetInfo = new GroupsGetInfo();
        groupsGetInfo.setApi_key(key);
        groupsGetInfo.setGroup_id(groupID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrGroupsGetInfo(groupsGetInfo);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement group = elements[i];
                    output = "Information about this group\n\n";
                    output = output + "Group ID : " + group.getAttribute(new QName("id")) + "\n";
                    output = output + "Group Name : " +
                            group.getFirstChildWithName(new QName("name")).getText();
                    output = output + "Group members : " +
                            group.getFirstChildWithName(new QName("members")).getText();
                    output = output + "Group Privacy : " +
                            group.getFirstChildWithName(new QName("privacy")).getText();
                    OMElement throttle = group.getFirstChildWithName(new QName("throttle"));
                    output = output + "Count : " +
                            throttle.getFirstChildWithName(new QName("count")).getText();
                    output = output + "Mode : " +
                            throttle.getFirstChildWithName(new QName("mode")).getText();
                    output = output + "Remaining : " +
                            throttle.getFirstChildWithName(new QName("remaining")).getText();
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrGroupsSearch(String text, String page, String perPage, String sharedSecret,
                                     String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        GroupsSearch groupsSearch = new GroupsSearch();
        groupsSearch.setApi_key(key);
        groupsSearch.setText(text);
        if (!"".equals(page.trim())) {
            groupsSearch.setPage(new BigInteger(page));
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            groupsSearch.setPer_page(new BigInteger(perPage));
        }
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrGroupsSearch(groupsSearch);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "Search results\n\n";
                    Iterator iterator = photos.getChildElements();
                    output = output + "Total results : " +
                            photos.getAttribute(new QName("total")).getAttributeValue() + "\n";
                    while (iterator.hasNext()) {
                        output = output + "Group\n";
                        OMElement group = (OMElement) iterator.next();
                        output = output + "Group ID : " +
                                group.getAttribute(new QName("nsid")).getAttributeValue() +
                                "\nGroup Name : " +
                                group.getAttribute(new QName("name")).getAttributeValue() +
                                "\n";
                        output = output + "Is 18+ : " +
                                group.getAttribute(new QName("eighteenplus")).getAttributeValue() +
                                "\n\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrGroupsPoolsAdd(String photoID, String groupID, String sharedSecret,
                                       String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "group_id" + groupID + "method" + "flickr.groups.browse" + "photo_id" + photoID;
        GroupsPoolsAdd groupsPoolsAdd = new GroupsPoolsAdd();
        groupsPoolsAdd.setApi_key(key);
        groupsPoolsAdd.setAuth_token(token);
        groupsPoolsAdd.setGroup_id(groupID);
        groupsPoolsAdd.setPhoto_id(photoID);
        String output = "";
        try {
            groupsPoolsAdd.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrGroupsPoolsAdd(groupsPoolsAdd);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = output + "Photo added to group's pool succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrGroupsPoolsGetContext(String photoID, String groupID, String sharedSecret,
                                              String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        GroupsPoolsGetContext groupsPoolsGetContext = new GroupsPoolsGetContext();
        groupsPoolsGetContext.setApi_key(key);
        groupsPoolsGetContext.setGroup_id(groupID);
        groupsPoolsGetContext.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrGroupsPoolsGetContext(groupsPoolsGetContext);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                output = "Next and previous photos for a photo in a group pool\n\n";
                OMElement prevphoto = elements[0];
                output = output + "Prevoius photo";
                output = output + "Photo ID : " +
                        prevphoto.getAttribute(new QName("id")).getAttributeValue() +
                        "\nPhoto secret : " +
                        prevphoto.getAttribute(new QName("secret")).getAttributeValue() +
                        "\n";
                output = output + "Photo Title : " +
                        prevphoto.getAttribute(new QName("title")).getAttributeValue() +
                        "\nPhoto URL : " +
                        prevphoto.getAttribute(new QName("url")).getAttributeValue() +
                        "\n\n";
                output = output + "Next photo";
                OMElement nextPhoto = elements[1];
                output = output + "Photo ID : " +
                        nextPhoto.getAttribute(new QName("id")).getAttributeValue() +
                        "\nPhoto secret : " +
                        nextPhoto.getAttribute(new QName("secret")).getAttributeValue() +
                        "\n";
                output = output + "Photo Title : " +
                        nextPhoto.getAttribute(new QName("title")).getAttributeValue() +
                        "\nPhoto URL : " +
                        nextPhoto.getAttribute(new QName("url")).getAttributeValue() +
                        "\n\n";

            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrGroupsPoolsGetGroups(String page, String perPage, String sharedSecret,
                                             String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.groups.pools.getGroups";
        GroupsPoolsGetGroups groupsPoolsGetGroups = new GroupsPoolsGetGroups();
        groupsPoolsGetGroups.setApi_key(key);
        groupsPoolsGetGroups.setAuth_token(token);
        if (!"".equals(page.trim())) {
            groupsPoolsGetGroups.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {

            groupsPoolsGetGroups.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
        String output = "";
        try {
            groupsPoolsGetGroups.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrGroupsPoolsGetGroups(groupsPoolsGetGroups);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement groups = elements[i];
                    output = "List of groups to which you can add photos\n\n" + "Total number of groups : " +
                            groups.getAttribute(new QName("total")).getAttributeValue() +
                            "\n";
                    Iterator iter = groups.getChildElements();
                    while (iter.hasNext()) {
                        OMElement group = (OMElement) iter.next();
                        output = output + "Group \nGroup ID : " +
                                group.getAttribute(new QName("nsid")).getAttributeValue() +
                                "\nGroup Name : " +
                                group.getAttribute(new QName("name")).getAttributeValue() +
                                "\n" + "Privacy : " +
                                group.getAttribute(new QName("privacy")).getAttributeValue() +
                                "\nPhotos : " +
                                group.getAttribute(new QName("photos")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetInfo(String photoID, String secret, String key, String host,
                                      String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetInfo photosGetInfo = new PhotosGetInfo();
        photosGetInfo.setApi_key(key);
        photosGetInfo.setPhoto_id(photoID);
        photosGetInfo.setSecret(secret);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetInfo(photosGetInfo);
            StatType statType = response.getStat();
            Token token = statType.getValue();
            if (token.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photo = elements[i];
                    output = "Information of the photo\n\n";
                    output = output + "Photo ID : " +
                            photo.getAttribute(new QName("id")).getAttributeValue() +
                            "\nSecret : " +
                            photo.getAttribute(new QName("secret")).getAttributeValue() +
                            "\n";
                    OMElement owner = photo.getFirstChildWithName(new QName("owner"));
                    output = output + "Owner name : " +
                            owner.getAttribute(new QName("realname")).getAttributeValue() + "\n";
                    output = output + "Owner Location : " +
                            owner.getAttribute(new QName("location")).getAttributeValue() + "\n";
                    OMElement title = photo.getFirstChildWithName(new QName("title"));
                    output = output + "Title : " + title.getText() + "\n";
                    OMElement description = photo.getFirstChildWithName(new QName("description"));
                    output = output + "Description : " + description.getText() + "\n";
                    OMElement dates = photo.getFirstChildWithName(new QName("dates"));
                    output = output + "Date Taken : " +
                            dates.getAttribute(new QName("taken")).getAttributeValue() + "\n";
                    OMElement notes = photo.getFirstChildWithName(new QName("notes"));
                    Iterator iter = notes.getChildElements();
                    while (iter.hasNext()) {
                        OMElement note = (OMElement) iter.next();
                        output = output + "Note : \n" + "Author Name : " +
                                note.getAttribute(new QName("authorname")).getAttributeValue() +
                                "\n" + "Author note : " + note.getText() + "\n";
                    }
                    OMElement tags = photo.getFirstChildWithName(new QName("tags"));
                    Iterator iter2 = tags.getChildElements();
                    while (iter2.hasNext()) {
                        OMElement tag = (OMElement) iter2.next();
                        output = output + "Tag : \n" + "Tag ID : " +
                                tag.getAttribute(new QName("id")).getAttributeValue() + "\n" + "Tag Author ID : " +
                                tag.getAttribute(new QName("author")).getAttributeValue() + "\n"  +
                                "Tag Text : " + tag.getText() + "\n";
                    }
                    OMElement urls = photo.getFirstChildWithName(new QName("urls"));
                    Iterator iter3 = urls.getChildElements();
                    while (iter3.hasNext()) {
                        OMElement url = (OMElement) iter3.next();
                        output = output + "Url : " + url.getText() + "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosAddTags(String photoID, String tags, String sharedSecret,
                                      String token, String key, String host,
                                      String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosAddTags photosAddTags = new PhotosAddTags();
        photosAddTags.setApi_key(key);
        photosAddTags.setPhoto_id(photoID);
        photosAddTags.setTags(tags);
        photosAddTags.setAuth_token(token);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.photos.addTags" + "photo_id" + photoID + "tags" + tags;

        String output = "";
        try {
            photosAddTags.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosAddTags(photosAddTags);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                output = "Tags added succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosDelete(String photoID, String sharedSecret,
                                     String token, String key, String host,
                                     String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosDelete photosDelete = new PhotosDelete();
        photosDelete.setApi_key(key);
        photosDelete.setPhoto_id(photoID);
        photosDelete.setAuth_token(token);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.photos.delete" + "photo_id" + photoID;

        String output = "";
        try {
            photosDelete.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosDelete(photosDelete);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                output = "Photo deleted succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetAllContexts(String photoID, String sharedSecret,
                                             String token, String key, String host,
                                             String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetAllContexts photosGetAllContexts = new PhotosGetAllContexts();
        photosGetAllContexts.setApi_key(key);
        photosGetAllContexts.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetAllContexts(photosGetAllContexts);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();

                OMElement set = elements[0];
                output = "Visible sets and pools the photo belongs to.\n\n";
                output = output + "Set ID : " +
                        set.getAttribute(new QName("id")).getAttributeValue() +
                        "\nTitle : " +
                        set.getAttribute(new QName("title")).getAttributeValue() +
                        "\n";
                OMElement photo = elements[1];
                output = output + "Pool ID : " +
                        photo.getAttribute(new QName("id")).getAttributeValue() +
                        "\nTitle : " +
                        photo.getAttribute(new QName("title")).getAttributeValue() +
                        "\n";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetContactsPhotos(String count, boolean friends, boolean single,
                                                boolean self, ExtrasBean extrasBean,
                                                String sharedSecret,
                                                String token, String key, String host,
                                                String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token;
        PhotosGetContactsPhotos photosGetContactsPhotos = new PhotosGetContactsPhotos();
        photosGetContactsPhotos.setApi_key(key);
        if (count != null) {
            photosGetContactsPhotos.setCount(new BigInteger(count));
            sig = sig + "count" + count;
        }
        String extras = processExtras(extrasBean);
        sig = sig + "extras" + extras;
        photosGetContactsPhotos.setExtras(extras);
        sig = sig + "formatrest";
        if (self) {
            photosGetContactsPhotos.setInclude_self(new BigInteger("1"));
            sig = sig + "include_self1";
        }
        if (friends) {
            photosGetContactsPhotos.setJust_friends(new BigInteger("1"));
            sig = sig + "just_friends1";
        }
        sig = sig + "method" + "flickr.photos.getContactsPhotos";
        if (single) {
        photosGetContactsPhotos.setSingle_photo(new BigInteger("1"));
            sig = sig + "single_photo1";
        }
        String output = "";
        try {
            photosGetContactsPhotos.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetContactsPhotos(photosGetContactsPhotos);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of recent photos from the calling users' contacts.\n\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output ="Photo\n" + "Photo ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n" + "Secret : " +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\nUsername : " +
                                photo.getAttribute(new QName("username")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetContactsPublicPhotos(String userID, String count, boolean friends,
                                                      boolean single, boolean self,
                                                      ExtrasBean extrasBean, String sharedSecret,
                                                      String token, String key, String host,
                                                      String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetContactsPublicPhotos photosGetContactsPublicPhotos =
                new PhotosGetContactsPublicPhotos();
        photosGetContactsPublicPhotos.setApi_key(key);
        photosGetContactsPublicPhotos.setUser_id(userID);
        if (count != null) {
            photosGetContactsPublicPhotos.setCount(new BigInteger(count));
        }
        photosGetContactsPublicPhotos.setExtras(processExtras(extrasBean));
        if (self) {
            photosGetContactsPublicPhotos.setInclude_self(new BigInteger("1"));
        }
        if (friends) {
            photosGetContactsPublicPhotos.setJust_friends(new BigInteger("1"));
        }
        if (single) {
        photosGetContactsPublicPhotos.setSingle_photo(new BigInteger("1"));
        }
        String output = "";
        try {
            Rsp response = flickrServiceStub
                    .flickrPhotosGetContactsPublicPhotos(photosGetContactsPublicPhotos);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of recent photos from the calling users' contacts.\n\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "Photo\n";
                        output = output + "Photo ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\n" + "Secret : " +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\nUsername : " +
                                photo.getAttribute(new QName("username")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetContext(String photoID, String sharedSecret,
                                         String token, String key, String host,
                                         String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetContext photosGetContext = new PhotosGetContext();
        photosGetContext.setApi_key(key);
        photosGetContext.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetContext(photosGetContext);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();

                OMElement prev = elements[0];
                output = "Next and previous photos for this photo which is in a photostream..\n\n";
                output = output + "Previous Photo \nPhoto ID : " +
                        prev.getAttribute(new QName("id")).getAttributeValue() +
                        "\nTitle : " +
                        prev.getAttribute(new QName("title")).getAttributeValue() +
                        "\n";
                output = output + "Secret : " +
                        prev.getAttribute(new QName("secret")).getAttributeValue() +
                        "\nURL : " +
                        prev.getAttribute(new QName("url")).getAttributeValue() +
                        "\n";
                OMElement next = elements[1];
                output = output + "Next Photo \nPhoto ID : " +
                        next.getAttribute(new QName("id")).getAttributeValue() +
                        "\nTitle : " +
                        next.getAttribute(new QName("title")).getAttributeValue() +
                        "\n";
                output = output + "Secret : " +
                        next.getAttribute(new QName("secret")).getAttributeValue() +
                        "\nURL : " +
                        next.getAttribute(new QName("url")).getAttributeValue() +
                        "\n";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetCounts(String dates, String takenDates, String sharedSecret,
                                        String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetCounts photosGetCounts = new PhotosGetCounts();
        String sig = sharedSecret + "api_key" + key + "auth_token" + token;

        photosGetCounts.setApi_key(key);
        if (dates != null && !"".equals(dates.trim())) {
            photosGetCounts.setDates(dates);
            sig = sig + "dates" + dates;
        }
        sig = sig + "formatrest" + "method" + "flickr.photos.getCounts";
        if (takenDates != null && !"".equals(takenDates.trim())) {
            photosGetCounts.setTaken_dates(takenDates);
            sig = sig + "taken_dates" + takenDates;
        }
        String output = "";
        try {
            photosGetCounts.setAuth_token(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetCounts(photosGetCounts);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output =
                            "List of photo counts for the given date ranges for the calling user.\n\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photocount = (OMElement) iterator.next();
                        output = output + "Photo Count\n" + "Count : " +
                                photocount.getAttribute(new QName("count")).getAttributeValue() +
                                "\nFrom Date : " +
                                photocount.getAttribute(new QName("fromdate")).getAttributeValue() +
                                "\n" + "To Date : " +
                                photocount.getAttribute(new QName("todate")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }


    public String flickrPhotosGetExif(String photoID, String secret, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetExif photosGetExif = new PhotosGetExif();
        photosGetExif.setApi_key(key);
        if (secret != null && !"".equals(secret.trim())) {
            photosGetExif.setSecret(secret);
        }
        photosGetExif.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetExif(photosGetExif);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of EXIF/TIFF/GPS tags for a given photo.\n\n";
                    output = output + "Photo ID : " +
                                photos.getAttribute(new QName("id")).getAttributeValue() +
                                "\nSecret : " +
                                photos.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        OMElement raw = photo.getFirstChildWithName(new QName("raw"));
                        output = output + "Exif\nTagSpace : " +
                                photo.getAttribute(new QName("tagspace")).getAttributeValue() +
                                "\nTag : " +
                                photo.getAttribute(new QName("tag")).getAttributeValue() +
                                "\nLabel : "  +
                                photo.getAttribute(new QName("label")).getAttributeValue() +
                                "\n" + "Raw : " + raw.getText();
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }


    public String flickrPhotosGetFavorites(String photoID, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetFavorites photosGetFavorites = new PhotosGetFavorites();
        photosGetFavorites.setApi_key(key);
        if (page != null && !"".equals(page.trim())) {
            photosGetFavorites.setPage(page);
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosGetFavorites.setPer_page(perPage);
        }
        photosGetFavorites.setPhoto_id(photoID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetFavorites(photosGetFavorites);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of people who have favorited this photo.\n\n" + "Photo ID : " +
                                photos.getAttribute(new QName("id")).getAttributeValue() +
                                "\nSecret : " +
                                photos.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement person = (OMElement) iterator.next();
                        output = output + "\nPerson\nUser ID : " +
                                person.getAttribute(new QName("nsid")).getAttributeValue() +
                                "\nUsername : " +
                                person.getAttribute(new QName("username")).getAttributeValue() +
                                "\nFavorited Date : "  +
                                person.getAttribute(new QName("favedate")).getAttributeValue() +
                                "\n";
                        
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

 public String flickrPhotosGetNotInSet(String minUpDate, String maxUpDate, String minTakDate, String maxTakDate, String privacy, ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetNotInSet photosGetNotInSet = new PhotosGetNotInSet();
        photosGetNotInSet.setApi_key(key);
     photosGetNotInSet.setAuth_token(token);
     String sig = sharedSecret + "api_key" + key + "auth_token" + token;
     String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosGetNotInSet.setExtras(extras);
         sig = sig + "extras" + extras;
     }
     sig = sig + "formatrest";
     if (minTakDate != null && !"".equals(minTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minTakDate);
         photosGetNotInSet.setMin_taken_date(date);
            sig = sig + "min_taken_date" + minTakDate;
        }
        if (minUpDate != null && !"".equals(minUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minUpDate);
         photosGetNotInSet.setMin_upload_date(date);
            sig = sig + "max_upload_date" + minUpDate;
        }
        if (maxTakDate != null && !"".equals(maxTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxTakDate);
         photosGetNotInSet.setMax_taken_date(date);
            sig = sig + "max_taken_date" + maxTakDate;
        }
        if (maxUpDate != null && !"".equals(maxUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxUpDate);
         photosGetNotInSet.setMax_upload_date(date);
            sig = sig + "max_upload_date" + maxUpDate;
        }
     sig = sig + "method" + "flickr.photos.getNotInSet";
        if (page != null && !"".equals(page.trim())) {
            photosGetNotInSet.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosGetNotInSet.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
     if (privacy != null && !"".equals(privacy.trim())) {
         photosGetNotInSet.setPrivacy_filter(new BigInteger(privacy));
         sig = sig + "privacy_filter" + privacy;

     }
        String output = "";
        try {
            photosGetNotInSet.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetNotInSet(photosGetNotInSet);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of your photos that are not part of any sets.\n\n";
                    output = output + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetPerms(String photoID, String sharedSecret,
                                      String token, String key, String host,
                                      String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetPerms photosGetPerms = new PhotosGetPerms();
        photosGetPerms.setApi_key(key);
        photosGetPerms.setPhoto_id(photoID);
        photosGetPerms.setAuth_token(token);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.photos.getPerms" + "photo_id" + photoID;

        String output = "";
        try {
            photosGetPerms.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetPerms(photosGetPerms);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "Ppermissions of this photo.\n\n";
                    output = output + "ID : " +
                                photos.getAttribute(new QName("id")).getAttributeValue() +
                                "\nIs Public : " +
                                photos.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photos.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photos.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetRecent(ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetRecent photosGetRecent = new PhotosGetRecent();
        photosGetRecent.setApi_key(key);
     String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosGetRecent.setExtras(extras);
     }
      if (page != null && !"".equals(page.trim())) {
            photosGetRecent.setPage(new BigInteger(page));
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosGetRecent.setPer_page(new BigInteger(perPage));
        }
     String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetRecent(photosGetRecent);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of the latest public photos uploaded to flickr.\n\n";
                    output = output + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetSizes(String photoID, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetSizes photosGetSizes = new PhotosGetSizes();
        photosGetSizes.setApi_key(key);
     photosGetSizes.setPhoto_id(photoID);
     String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosGetSizes(photosGetSizes);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "View the photo.\n\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement size = (OMElement) iterator.next();
                        output = output + "\nSize\nLabel : " +
                                size.getAttribute(new QName("label")).getAttributeValue() +
                                "\nWidth : " +
                                size.getAttribute(new QName("width")).getAttributeValue() +
                                "\nHeight : "  +
                                size.getAttribute(new QName("height")).getAttributeValue() +
                                "\n" + "Source : " +
                                size.getAttribute(new QName("source")).getAttributeValue() +
                                "\nURL : " +
                                size.getAttribute(new QName("url")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetUntagged(String minUpDate, String maxUpDate, String minTakDate, String maxTakDate, String privacy, ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetUntagged photosGetUntagged = new PhotosGetUntagged();
        photosGetUntagged.setApi_key(key);
     photosGetUntagged.setAuth_token(token);
     String sig = sharedSecret + "api_key" + key + "auth_token" + token;
     String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosGetUntagged.setExtras(extras);
         sig = sig + "extras" + extras;
     }
     sig = sig + "formatrest";
     if (minTakDate != null && !"".equals(minTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minTakDate);
         photosGetUntagged.setMin_taken_date(date);
            sig = sig + "min_taken_date" + minTakDate;
        }
        if (minUpDate != null && !"".equals(minUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minUpDate);
         photosGetUntagged.setMin_upload_date(date);
            sig = sig + "max_upload_date" + minUpDate;
        }
        if (maxTakDate != null && !"".equals(maxTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxTakDate);
         photosGetUntagged.setMax_taken_date(date);
            sig = sig + "max_taken_date" + maxTakDate;
        }
        if (maxUpDate != null && !"".equals(maxUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxUpDate);
         photosGetUntagged.setMax_upload_date(date);
            sig = sig + "max_upload_date" + maxUpDate;
        }
        sig = sig + "method" + "flickr.photos.getUntagged";
        if (page != null && !"".equals(page.trim())) {
            photosGetUntagged.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosGetUntagged.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
     if (privacy != null && !"".equals(privacy.trim())) {
         photosGetUntagged.setPrivacy_filter(new BigInteger(privacy));
         sig = sig + "privacy_filter" + privacy;

     }
        String output = "";
        try {
            photosGetUntagged.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetUntagged(photosGetUntagged);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of your photos with no tags.\n\n";
                    output = output + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetWithGeoData(String minUpDate, String maxUpDate, String minTakDate, String maxTakDate, String privacy, ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetWithGeoData photosGetWithGeoData = new PhotosGetWithGeoData();
        photosGetWithGeoData.setApi_key(key);
     photosGetWithGeoData.setAuth_token(token);
     String sig = sharedSecret + "api_key" + key + "auth_token" + token;
     String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosGetWithGeoData.setExtras(extras);
         sig = sig + "extras" + extras;
     }
     sig = sig + "formatrest";
     if (minTakDate != null && !"".equals(minTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minTakDate);
         photosGetWithGeoData.setMin_taken_date(date);
            sig = sig + "min_taken_date" + minTakDate;
        }
        if (minUpDate != null && !"".equals(minUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minUpDate);
         photosGetWithGeoData.setMin_upload_date(date);
            sig = sig + "max_upload_date" + minUpDate;
        }
        if (maxTakDate != null && !"".equals(maxTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxTakDate);
         photosGetWithGeoData.setMax_taken_date(date);
            sig = sig + "max_taken_date" + maxTakDate;
        }
        if (maxUpDate != null && !"".equals(maxUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxUpDate);
         photosGetWithGeoData.setMax_upload_date(date);
            sig = sig + "max_upload_date" + maxUpDate;
        }
        sig = sig + "method" + "flickr.photos.getWithGeoData";
        if (page != null && !"".equals(page.trim())) {
            photosGetWithGeoData.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosGetWithGeoData.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
     if (privacy != null && !"".equals(privacy.trim())) {
         photosGetWithGeoData.setPrivacy_filter(new BigInteger(privacy));
         sig = sig + "privacy_filter" + privacy;
     }
     String output = "";
        try {
            photosGetWithGeoData.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetWithGeoData(photosGetWithGeoData);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of your geo-tagged photos.\n\n";
                    output = output + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosGetWithoutGeoData(String minUpDate, String maxUpDate, String minTakDate, String maxTakDate, String privacy, ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosGetWithoutGeoData photosGetWithoutGeoData = new PhotosGetWithoutGeoData();
        photosGetWithoutGeoData.setApi_key(key);
     photosGetWithoutGeoData.setAuth_token(token);
     String sig = sharedSecret + "api_key" + key + "auth_token" + token;
     String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosGetWithoutGeoData.setExtras(extras);
         sig = sig + "extras" + extras;
     }
     sig = sig + "formatrest";
     if (minTakDate != null && !"".equals(minTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minTakDate);
         photosGetWithoutGeoData.setMin_taken_date(date);
            sig = sig + "min_taken_date" + minTakDate;
        }
        if (minUpDate != null && !"".equals(minUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minUpDate);
         photosGetWithoutGeoData.setMin_upload_date(date);
            sig = sig + "max_upload_date" + minUpDate;
        }
        if (maxTakDate != null && !"".equals(maxTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxTakDate);
         photosGetWithoutGeoData.setMax_taken_date(date);
            sig = sig + "max_taken_date" + maxTakDate;
        }
        if (maxUpDate != null && !"".equals(maxUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxUpDate);
         photosGetWithoutGeoData.setMax_upload_date(date);
            sig = sig + "max_upload_date" + maxUpDate;
        }
        sig = sig + "method" + "flickr.photos.getWithoutGeoData";
        if (page != null && !"".equals(page.trim())) {
            photosGetWithoutGeoData.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosGetWithoutGeoData.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
     if (privacy != null && !"".equals(privacy.trim())) {
         photosGetWithoutGeoData.setPrivacy_filter(new BigInteger(privacy));
         sig = sig + "privacy_filter" + privacy;
     }
     String output = "";
        try {
            photosGetWithoutGeoData.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosGetWithoutGeoData(photosGetWithoutGeoData);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of your photos which haven't been geo-tagged.\n\n";
                    output = output + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosRecentlyUpdated(String minDate, ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosRecentlyUpdated photosRecentlyUpdated = new PhotosRecentlyUpdated();
        photosRecentlyUpdated.setApi_key(key);
     photosRecentlyUpdated.setAuth_token(token);
     String sig = sharedSecret + "api_key" + key + "auth_token" + token;
     String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosRecentlyUpdated.setExtras(extras);
         sig = sig + "extras" + extras;
     }
     sig = sig + "formatrest";
        sig = sig + "method" + "flickr.photos.recentlyUpdated";
     if (minDate != null && !"".equals(minDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minDate);
         photosRecentlyUpdated.setMin_date(date);
            sig = sig + "min_date" + minDate;
        }
        if (page != null && !"".equals(page.trim())) {
            photosRecentlyUpdated.setPage(new BigInteger(page));
            sig = sig + "page" + page;
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosRecentlyUpdated.setPer_page(new BigInteger(perPage));
            sig = sig + "per_page" + perPage;
        }
     String output = "";
        try {
            photosRecentlyUpdated.setApi_sig(DigestUtils.md5Hex(sig));
            Rsp response = flickrServiceStub.flickrPhotosRecentlyUpdated(photosRecentlyUpdated);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "List of your photos that have been recently created or which have been recently modified.\n\n";
                    output = output + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n";
                    output = output + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n";
                        output = output + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n";
                        output = output + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\nLast Updated : "  +
                                photo.getAttribute(new QName("lastupdate")).getAttributeValue() +
                                "\n";

                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosRemoveTag(String tagID, String sharedSecret, String token,
                                        String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosRemoveTag photosRemoveTag = new PhotosRemoveTag();
        photosRemoveTag.setApi_key(key);
        photosRemoveTag.setTag_id(tagID);
        String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosRemoveTag(photosRemoveTag);
            StatType statType = response.getStat();
            Token retToken = statType.getValue();
            if (retToken.equals(StatType._ok)) {
                output = "Removed tag succesfully";
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosSearch(String userID, String tags, AnyOrAll tagMode, String text, String minUpDate, String maxUpDate, String minTakDate, String maxTakDate, String license, SortOrder sort, String privacy, String accuracy, String machineTags, AnyOrAll machineTagsMode, String grpID, ExtrasBean extrasBean, String page, String perPage, String sharedSecret,
                                      String token, String key, String host, String port) {
        FlickrServiceStub flickrServiceStub = getStub(host, port);
        PhotosSearch photosSearch = new PhotosSearch();
        photosSearch.setApi_key(key);
        String extras = processExtras(extrasBean);
     if (!"".equals(extras.trim())) {
         photosSearch.setExtras(extras);
     }
     if (minTakDate != null && !"".equals(minTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minTakDate);
         photosSearch.setMin_taken_date(date);
        }
        if (minUpDate != null && !"".equals(minUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(minUpDate);
         photosSearch.setMin_upload_date(date);
        }
        if (maxTakDate != null && !"".equals(maxTakDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxTakDate);
         photosSearch.setMax_taken_date(date);
        }
        if (maxUpDate != null && !"".equals(maxUpDate.trim())) {
         UnixTimeStamp date = new UnixTimeStamp();
         date.setUnixTimeStamp(maxUpDate);
         photosSearch.setMax_upload_date(date);
        }
        if (page != null && !"".equals(page.trim())) {
            photosSearch.setPage(new BigInteger(page));
        }
        if (perPage != null && !"".equals(perPage.trim())) {
            photosSearch.setPer_page(new BigInteger(perPage));
        }
     if (privacy != null && !"".equals(privacy.trim())) {
         photosSearch.setPrivacy_filter(new BigInteger(privacy));
     }
        if (userID != null && !"".equals(userID.trim())) {
            photosSearch.setUser_id(userID);
        }
        if (tags != null && !"".equals(tags.trim())) {
            photosSearch.setTags(tags);
        }
        if (tagMode != null) {
            photosSearch.setTag_mode(tagMode);
        }
        if (text != null && !"".equals(text.trim())) {
            photosSearch.setText(text);
        }
        if (license != null && !"".equals(license.trim())) {
            photosSearch.setLicense(license);
        }
        if (sort != null) {
            photosSearch.setSort(sort);
        }
        if (accuracy != null && !"".equals(accuracy.trim())) {
            photosSearch.setAccuracy(new BigInteger(accuracy));
        }
        if (machineTags != null && !"".equals(machineTags.trim())) {
            photosSearch.setMachine_tags(machineTags);
        }
        if (machineTagsMode != null) {
            photosSearch.setMachine_tag_mode(machineTagsMode);
        }
        if (grpID != null && !"".equals(grpID.trim())) {
            photosSearch.setGroup_id(grpID);
        }
     String output = "";
        try {
            Rsp response = flickrServiceStub.flickrPhotosSearch(photosSearch);
            StatType statType = response.getStat();
            Token rspToken = statType.getValue();
            if (rspToken.equals(StatType._ok)) {
                OMElement[] elements = response.getExtraElement();
                for (int i = 0; i < elements.length; i++) {
                    OMElement photos = elements[i];
                    output = "Search Results.\n\n" + "Page : " +
                                photos.getAttribute(new QName("page")).getAttributeValue() +
                                "\nPages : " +
                                photos.getAttribute(new QName("pages")).getAttributeValue() +
                                "\n" + "Per Page : " +
                                photos.getAttribute(new QName("perpage")).getAttributeValue() +
                                "\nTotal : " +
                                photos.getAttribute(new QName("total")).getAttributeValue() +
                                "\n";
                    Iterator iterator = photos.getChildElements();
                    while (iterator.hasNext()) {
                        OMElement photo = (OMElement) iterator.next();
                        output = output + "\nPhoto\nPhoto ID : " +
                                photo.getAttribute(new QName("id")).getAttributeValue() +
                                "\nOwner ID : " +
                                photo.getAttribute(new QName("owner")).getAttributeValue() +
                                "\nSecret : "  +
                                photo.getAttribute(new QName("secret")).getAttributeValue() +
                                "\n" + "Title : " +
                                photo.getAttribute(new QName("title")).getAttributeValue() +
                                "\nIs Public : " +
                                photo.getAttribute(new QName("ispublic")).getAttributeValue() +
                                "\nIs Friend : "  +
                                photo.getAttribute(new QName("isfriend")).getAttributeValue() +
                                "\n" + "Is Family : " +
                                photo.getAttribute(new QName("isfamily")).getAttributeValue() +
                                "\n";
                    }
                }
            } else {
                output = processPeopleError(response.getErr());
            }
        } catch (RemoteException e) {
            output = e.getMessage();
        }
        return output;
    }

    public String flickrPhotosSetDates(String photoID, String datePosted, String dateTaken, String granularity, String sharedSecret,
                                          String token, String key, String host, String port) {
            FlickrServiceStub flickrServiceStub = getStub(host, port);
            PhotosSetDates photosSetDates = new PhotosSetDates();
            photosSetDates.setApi_key(key);
        photosSetDates.setAuth_token(token);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token;
            if (datePosted != null && !"".equals(datePosted.trim())) {
                photosSetDates.setDate_posted(datePosted);
                sig = sig + "date_posted" + datePosted;
            }
        if (dateTaken != null && !"".equals(dateTaken.trim())) {
                photosSetDates.setDate_taken(dateTaken);
                sig = sig + "date_taken" + dateTaken;
            }
        if (granularity != null && !"".equals(granularity.trim())) {
                photosSetDates.setDate_taken_granularity(granularity);
                sig = sig + "date_taken_granularity" + granularity;
            }
            photosSetDates.setPhoto_id(photoID);
            String output = "";
            try {
                Rsp response = flickrServiceStub.flickrPhotosSetDates(photosSetDates);
                StatType statType = response.getStat();
                Token rspToken = statType.getValue();
                if (rspToken.equals(StatType._ok)) {
                    output = "Set Dates succesfully.\n\n";

               } else {
                    output = processPeopleError(response.getErr());
                }
            } catch (RemoteException e) {
                output = e.getMessage();
            }
            return output;
        }

        public String flickrPhotosSetMeta(String photoID, String title, String description, String sharedSecret,
                                          String token, String key, String host, String port) {
            FlickrServiceStub flickrServiceStub = getStub(host, port);
            PhotosSetMeta photosSetMeta = new PhotosSetMeta();
            photosSetMeta.setApi_key(key);
        photosSetMeta.setAuth_token(token);
            photosSetMeta.setTitle(title);
            photosSetMeta.setDescription(description);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "description" + description + "formatrest" +
                "method" + "flickr.photos.setMeta" + "photo_id" + photoID + "title" + title;
            photosSetMeta.setPhoto_id(photoID);
            String output = "";
            try {
                photosSetMeta.setApi_sig(DigestUtils.md5Hex(sig));
                Rsp response = flickrServiceStub.flickrPhotosSetMeta(photosSetMeta);
                StatType statType = response.getStat();
                Token rspToken = statType.getValue();
                if (rspToken.equals(StatType._ok)) {
                    output = "Set Meta Data succesfully.\n\n";

               } else {
                    output = processPeopleError(response.getErr());
                }
            } catch (RemoteException e) {
                output = e.getMessage();
            }
            return output;
        }

    public String flickrPhotosSetPerms(String photoID, boolean isPublic, boolean isFriend, boolean isFamily, String permComment, String permMetaData, String sharedSecret,
                                          String token, String key, String host, String port) {
            FlickrServiceStub flickrServiceStub = getStub(host, port);
            PhotosSetPerms photosSetPerms = new PhotosSetPerms();
            photosSetPerms.setApi_key(key);
        photosSetPerms.setAuth_token(token);
        photosSetPerms.setPerm_comment(new BigInteger(permComment));
        photosSetPerms.setPerm_addmeta(new BigInteger(permMetaData));
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                        "method" + "flickr.photos.setPerms";

        if (isFamily) {
        photosSetPerms.setIs_family(new BigInteger("1"));
            sig = sig + "is_family1";
        } else {
            photosSetPerms.setIs_family(new BigInteger("0"));
            sig = sig + "is_family0";
        }
        if (isFriend) {
        photosSetPerms.setIs_friend(new BigInteger("1"));
            sig = sig + "is_friend1";
        } else {
            photosSetPerms.setIs_friend(new BigInteger("0"));
            sig = sig + "is_friend0";
        }
        if (isPublic) {
        photosSetPerms.setIs_public(new BigInteger("1"));
            sig = sig + "is_public1";
        } else {
            photosSetPerms.setIs_public(new BigInteger("0"));
            sig = sig + "is_public0";
        }
        sig = sig  + "perm_addmeta" + permMetaData + "perm_comment" + permComment + "photo_id" + photoID;
        photosSetPerms.setPhoto_id(photoID);
            String output = "";
            try {
                photosSetPerms.setApi_sig(DigestUtils.md5Hex(sig));
                Rsp response = flickrServiceStub.flickrPhotosSetPerms(photosSetPerms);
                StatType statType = response.getStat();
                Token rspToken = statType.getValue();
                if (rspToken.equals(StatType._ok)) {
                    output = "Set Permissions succesfully.\n\n";

               } else {
                    output = processPeopleError(response.getErr());
                }
            } catch (RemoteException e) {
                output = e.getMessage();
            }
            return output;
        }

     public String flickrPhotosSetTags(String photoID, String tags, String sharedSecret,
                                          String token, String key, String host, String port) {
            FlickrServiceStub flickrServiceStub = getStub(host, port);
            PhotosSetTags photosSetTags = new PhotosSetTags();
            photosSetTags.setApi_key(key);
        photosSetTags.setAuth_token(token);
         photosSetTags.setTags(tags);
        String sig = sharedSecret + "api_key" + key + "auth_token" + token + "formatrest" +
                "method" + "flickr.photos.setTags" + "photo_id" + photoID + "tags" + tags;
            photosSetTags.setPhoto_id(photoID);
            String output = "";
            try {
                photosSetTags.setApi_sig(DigestUtils.md5Hex(sig));
                Rsp response = flickrServiceStub.flickrPhotosSetTags(photosSetTags);
                StatType statType = response.getStat();
                Token rspToken = statType.getValue();
                if (rspToken.equals(StatType._ok)) {
                    output = "Set Tags succesfully.\n\n";

               } else {
                    output = processPeopleError(response.getErr());
                }
            } catch (RemoteException e) {
                output = e.getMessage();
            }
            return output;
        }

    private String processExtras(ExtrasBean extras) {

        String extrasString = "";

        if (extras.isLicense()) {
            extrasString = "license,";
        }
        if (extras.isDate_upload()) {
            extrasString = extrasString + "date_upload,";
        }
        if (extras.isDate_taken()) {
            extrasString = extrasString + "date_taken,";
        }
        if (extras.isOwner_name()) {
            extrasString = extrasString + "owner_name,";
        }
        if (extras.isIcon_server()) {
            extrasString = extrasString + "icon_server,";
        }
        if (extras.isOriginal_format()) {
            extrasString = extrasString + "original_format,";
        }
        if (extras.isLast_update()) {
            extrasString = extrasString + "last_update,";
        }
        if (extras.isGeo()) {
            extrasString = extrasString + "geo,";
        }
        if (extras.isTags()) {
            extrasString = extrasString + "tags,";
        }
        if (extras.isMachine_tags()) {
            extrasString = extrasString + "machine_tags";
        }
        return extrasString;
    }

    private FlickrServiceStub getStub(String host, String port) {
        FlickrServiceStub flickrServiceStub = null;
        try {
            String address = "http://" + host + ":" + port + "/services/rest/";
            flickrServiceStub = new FlickrServiceStub(address);
        } catch (AxisFault axisFault) {
            axisFault
                    .printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return flickrServiceStub;
    }

}
