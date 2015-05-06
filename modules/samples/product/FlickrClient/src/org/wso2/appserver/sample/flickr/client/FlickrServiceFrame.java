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

import org.apache.commons.codec.digest.DigestUtils;
import org.wso2.www.types.flickr.client.AnyOrAll;
import org.wso2.www.types.flickr.client.Filter;
import org.wso2.www.types.flickr.client.SortOrder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

public class FlickrServiceFrame extends JFrame {
    private static final String READ = "read";
    private static final String WRITE = "write";
    private static final String DELETE = "delete";
    
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTabbedPane peopleOperationPane;
    private JButton findByEmailInvoke;
    private JTextArea findByEmailOutput;
    private JTextField txtPeopleUsername;
    private JTextField txtPeopleEmail;
    private JEditorPane findByUsernameOutput;
    private JButton findByUsernameInvoke;
    private JLabel txtUserID;
    private JTextField txtPeopleGetInfo;
    private JTextArea getInfoOutput;
    private JButton getInfoInvoke;
    private JTextField txtGetPublicGroups;
    private JButton getPublicGroupsInvoke;
    private JTextArea getPublicGroupsOutput;
    private JTextField txtGetPublicPhotos;
    private JButton getPublicPhotosInvoke;
    private JTextArea getPublicPhotosOutput;
    private JButton getUploadStatusInvoke;
    private JTextArea getUploadStatusOutput;
    private JButton photosGetInfoInvoke;
    private JButton photosDeleteInvoke;
    private JComboBox cmbGetContactsPhotos;
    private JButton getContactsPhotosInvoke;
    private JCheckBox chkGetContactsPhotosLicense;
    private JCheckBox chkGetContactsPhotosUploadDate;
    private JCheckBox chkGetContactsPhotosDateTaken;
    private JCheckBox chkGetContactsPhotosOwner;
    private JCheckBox chkGetContactsPhotosServer;
    private JCheckBox chkGetContactsPhotosOriginal;
    private JCheckBox chkGetContactsPhotosLastUpdate;
    private JComboBox cmbGetContactsPublicPhotosCount;
    private JComboBox cmbGetFavoritesPerPage;
    private JComboBox cmbGetNotInSetPerPage;
    private JComboBox cmbGetRecentPerPage;
    private JTabbedPane activityOperationPane;
    private JComboBox cmbSearchPrivacy;
    private JTextField txtSearchPage;
    private JComboBox cmbSearchPerPage;
    private JComboBox cmbSearchAccuracy;
    private JTextField txtSearchMachine;
    private JTextField txtSearchMinTakDate;
    private JComboBox cmbSearchSort;
    private JTextField txtSearchLicense;
    private JTextArea searchOutput;
    private JButton searchInvoke;
    private JComboBox cmbSearchMachineMode;
    private JTextField txtSearchGroupID;
    private JTextField txtMaxTakDate;
    private JTextField txtSearchMaxUpDate;
    private JTextField txtSearchMinUpDate;
    private JComboBox cmbSearchTagMode;
    private JTextField txtSearchTags;
    private JTextField txtSearchText;
    private JTextField txtSearchUserID;
    private JTextField txtGetPermsPhotoID;
    private JButton getPermsInvoke;
    private JTextField txtGetRecentPage;
    private JButton getRecentInvoke;
    private JCheckBox chkGetRecentLicense;
    private JCheckBox chkGetRecentDateUp;
    private JCheckBox chkGetRecentDateTak;
    private JCheckBox chkGetRecentOwner;
    private JCheckBox chkGetRecentServer;
    private JCheckBox chkGetRecentOriginal;
    private JCheckBox chkGetRecentLastUp;
    private JCheckBox chkGetRecentGeo;
    private JCheckBox chkGetRecentTags;
    private JCheckBox chkGetRecentMachine;
    private JTextArea getPermsOutput;
    private JTextArea getRecentOutput;
    private JTextField txtGetUntaggedMinUpDate;
    private JTextField txtGetUntaggedMinTakDate;
    private JTextField txtGetUntaggedMaxTakDate;
    private JTextField txtGetUntaggedMaxUpDate;
    private JTextField txtGetUntaggedPage;
    private JComboBox cmbGetUntaggedPrivacy;
    private JComboBox cmbGetUntaggedPerPage;
    private JButton getUntaggedInvoke;
    private JTextArea getSizesOutput;
    private JButton getSizesInvoke;
    private JTextField txtGetSizesPhotoID;
    private JTabbedPane photosOperationPane;
    private JTextField txtPhotosGetInfoPhotoID;
    private JTextField txtPhotosGetInfoSecret;
    private JTextArea photosGetInfoOutput;
    private JTextField txtPhotosAddTagsPhotoID;
    private JTextField txtAddTags;
    private JButton photosAddTagsInvoke;
    private JTextField txtPhotosDeletePhotoID;
    private JTextArea photosDeleteOutput;
    private JTextField txtGetAllContextsPhotoID;
    private JButton getAllContextsInvoke;
    private JTextArea photosAddTagsOutput;
    private JCheckBox chkGetContactsPhotosFriends;
    private JCheckBox chkGetContactsPhotosSingle;
    private JCheckBox chkGetContactsPhotosSelf;
    private JTextField txtGetContactsPublicPhotosUserID;
    private JTextArea getAllContextsOutput;
    private JTextArea getContactsPhotosOutput;
    private JCheckBox chkGetContactsPublicPhotosSingle;
    private JCheckBox chkGetContactsPublicPhotosSelf;
    private JButton getContactsPublicPhotosInvoke;
    private JCheckBox chkGetContactsPublicPhotosFriends;
    private JTextField txtRemoveTagTadID;
    private JButton removeTagInvoke;
    private JCheckBox last_updateCheckBox;
    private JCheckBox original_formatCheckBox;
    private JCheckBox icon_serverCheckBox;
    private JCheckBox date_uploadCheckBox;
    private JCheckBox date_takenCheckBox;
    private JCheckBox owner_nameCheckBox;
    private JCheckBox licenseCheckBox;
    private JCheckBox chkSearchMachine;
    private JCheckBox chkSearchTags;
    private JCheckBox chkSearchGeo;
    private JCheckBox chkSearchLastUp;
    private JCheckBox chkSearchOriginal;
    private JCheckBox chkSearchDateTak;
    private JCheckBox chkSearchOwner;
    private JCheckBox chkSearchServer;
    private JCheckBox chkSearchLicense;
    private JCheckBox chkSearchDateUp;
    private JTextArea removeTagOutput;
    private JComboBox cmbRecentlyUpdatedPerPage;
    private JCheckBox chkRecentlyUpdatedMachine;
    private JCheckBox chkRecentlyUpdatedTags;
    private JCheckBox chkRecentlyUpdatedGeo;
    private JCheckBox chkRecentlyUpdatedLastUp;
    private JCheckBox chkRecentlyUpdatedOriginal;
    private JCheckBox chkRecentlyUpdatedDateUp;
    private JCheckBox chkRecentlyUpdatedDateTak;
    private JCheckBox chkRecentlyUpdatedOwner;
    private JCheckBox chkRecentlyUpdatedServer;
    private JCheckBox chkRecentlyUpdatedLicense;
    private JButton recentlyUpdatedInvoke;
    private JTextField txtRecentlyUpdatedPage;
    private JTextField txtRecentlyUpdatedMinDate;
    private JTextArea getWithGeoDataOutput;
    private JCheckBox chkGetWithGeoDataMachine;
    private JCheckBox chkGetWithGeoDataTags;
    private JCheckBox chkGetWithGeoDataGeo;
    private JCheckBox chkGetWithGeoDataLastUp;
    private JCheckBox chkGetWithGeoDataOriginal;
    private JCheckBox chkGetWithGeoDataServer;
    private JCheckBox chkGetWithGeoDataOwner;
    private JCheckBox chkGetWithGeoDataDateTak;
    private JCheckBox chkGetWithGeoDataDateUp;
    private JButton getWithGeoDataInvoke;
    private JComboBox cmbGetWithGeoDataPrivacy;
    private JComboBox cmbGetWithGeoDataPerPage;
    private JTextField txtGetWithGeoDataPage;
    private JTextField txtGetWithGeoDataMinTakDate;
    private JTextField txtGetWithGeoDataMaxTakDate;
    private JCheckBox chkGetWithGeoDataLicense;
    private JTextField txtGetWithGeoDataMaxUpDate;
    private JTextField txtGetWithGeoDataMinUpDate;
    private JTextArea getUntaggedOutput;
    private JCheckBox chkGetUntaggedMachine;
    private JCheckBox chkGetUntaggedTags;
    private JCheckBox chkGetUntaggedGeo;
    private JCheckBox chkGetUntaggedLastUp;
    private JCheckBox chkGetUntaggedOriginal;
    private JCheckBox chkGetUntaggedServer;
    private JCheckBox chkGetUntaggedOwner;
    private JCheckBox chkGetUntaggedDateUp;
    private JCheckBox chkGetUntaggedLicense;
    private JCheckBox chkGetUntaggedDateTak;
    private JTextArea getWithoutGeoDataOutput;
    private JCheckBox chkGetWithoutGeoDataMachine;
    private JCheckBox chkGetWithoutGeoDataServer;
    private JCheckBox chkGetWithoutGeoDataOriginal;
    private JCheckBox chkGetWithoutGeoDataLastUp;
    private JCheckBox chkGetWithoutGeoDataTags;
    private JCheckBox chkGetWithoutGeoDataOwner;
    private JCheckBox chkGetWithoutGeoDataGeo;
    private JCheckBox chkGetWithoutGeoDataDateTak;
    private JCheckBox chkGetWithoutGeoDataDateUp;
    private JCheckBox chkGetWithoutGeoDataLicense;
    private JButton getWithoutGeoDataInvoke;
    private JComboBox cmbGetWithoutGeoDataPrivacy;
    private JComboBox cmbGetWithoutGeoDataPerPage;
    private JTextField txtGetWithoutGeoDataPage;
    private JTextField txtGetWithoutGeoDataMinTakDate;
    private JPasswordField txtGetWithoutGeoDataMaxTakDate;
    private JTextField txtGetWithoutGeoDataMaxUpDate;
    private JTextField txtGetWithoutGeoDataMinUpDate;
    private JTextArea getNotInSetOutput;
    private JCheckBox chkGetNotInSetMachine;
    private JCheckBox chkGetNotInSetTags;
    private JCheckBox chkGetNotInSetGeo;
    private JCheckBox chkGetNotInSetLastUp;
    private JCheckBox chkGetNotInSetOriginal;
    private JCheckBox chkGetNotInSetDateUp;
    private JCheckBox chkGetNotInSetDateTak;
    private JCheckBox chkGetNotInSetOwner;
    private JCheckBox chkGetNotInSetServer;
    private JCheckBox chkGetNotInSetLicense;
    private JButton getNotInSetInvoke;
    private JComboBox cmbGetNotInSetPrivacy;
    private JTextField txtGetNotInSetPage;
    private JTextField txtGetNotInSetMaxTakDate;
    private JTextField txtGetNotInSetMinTakDate;
    private JTextField txtGetNotInSetMaxUpDate;
    private JTextField txtGetNotInSetMinUpDate;
    private JTextArea getFavoritesOutput;
    private JButton getFavoritesInvoke;
    private JTextField txtGetFavoritesPage;
    private JTextField txtGetFavoritesPhotoID;
    private JTextArea getContactsPublicPhotosOutput;
    private JTextArea getExifOutput;
    private JButton getExifInvoke;
    private JTextField txtGetExifSecret;
    private JTextField txtGetExifPhotoID;
    private JTextArea recentlyUpdatedOutput;
    private JTextArea getCountsOutput;
    private JButton getCountsInvoke;
    private JTextField txtGetCountsDatesTaken;
    private JTextField txtGetCountsDates;
    private JTextField txtGetContextPhotoID;
    private JButton getContextInvoke;
    private JTextArea getContextOutput;
    private JTextField txtSetDatesPhotoID;
    private JTextField txtSetDatesPosted;
    private JTextField txtSetDatesTaken;
    private JTextField txtSetDatesGranularity;
    private JButton setDatesInvoke;
    private JTextArea setDatesOutput;
    private JTextField txtSetMetaPhotoID;
    private JTextField txtSetMetaTitle;
    private JTextField txtSetMetaDescription;
    private JButton setMetaInvoke;
    private JTextArea setMetaOutput;
    private JTextField txtSetPermsPhotoID;
    private JComboBox cmbSetPermsComments;
    private JComboBox cmbSetPermsMeta;
    private JCheckBox chkSetPermsFriends;
    private JCheckBox chkSetPermsFamily;
    private JCheckBox chkSetPermsPublic;
    private JButton setPermsInvoke;
    private JTextArea setPermsOutput;
    private JTextField txtSetTagsPhotoID;
    private JTextField txtSetTagsTags;
    private JButton SetTagsInvoke;
    private JTextArea setTagsOutput;
    private JTextField txtUserCommentsPage;
    private JComboBox cmbUserCommentsPerPage;
    private JButton userCommentsInvoke;
    private JTextArea userCommentsOutput;
    private JTextField txtUserPhotosTimeFrame;
    private JTextField txtUserPhotosPage;
    private JComboBox cmbUserPhotosPerPage;
    private JButton userPhotosInvoke;
    private JTextArea userPhotosOutput;
    private JTabbedPane blogsOperationPane;
    private JTextField txtPostPhotoBlogID;
    private JTextField txtPostPhotoPhotoID;
    private JTextField txtPostPhotoTitle;
    private JTextField txtPostPhotoDescription;
    private JTextField txtPostPhotoPassword;
    private JButton postPhotoInvoke;
    private JTextArea postPhotoOutput;
    private JTabbedPane photoSetsCommentsOperationPane;
    private JTextField txtPhotoSetsCommAddID;
    private JTextField PhotoSetsCommAddComment;
    private JButton photoSetsCommAddInvoke;
    private JTextArea photoSetsCommAddOutput;
    private JTextField txtPhotoSetsCommDelID;
    private JButton photoSetsCommDelInvoke;
    private JTextArea photoSetsCommDelOutput;
    private JTextField txtPhotoSetsCommEditID;
    private JTextField txtPhotoSetsCommEditComment;
    private JButton photoSetsCommEditInvoke;
    private JTextArea PhotoSetsCommEditOutput;
    private JTextField txtPhotoSetsCommGetID;
    private JButton photoSetsCommGetInvoke;
    private JTextArea photoSetsCommGetOutput;
    private JTabbedPane contactsOperationPane;
    private JComboBox cmbContactsGetFilter;
    private JTextField txtContactsGetPage;
    private JComboBox cmbContactsGetPerPage;
    private JButton contactsGetInvoke;
    private JTextArea contactsGetOutput;
    private JTextField txtContactsGetPubID;
    private JTextField txtContactsGetPubPage;
    private JComboBox cmbContactsGetPubPerPage;
    private JButton contactsGetPubInvoke;
    private JTextArea contactsGetPubOutput;
    private JTabbedPane favoritesOperationPane;
    private JTextField txtFavoritesAddID;
    private JButton favoritesAddInvoke;
    private JTextArea favoritesAddOutput;
    private JTextField txtFavoritesGetID;
    private JTextField txtFavoritesGetPage;
    private JComboBox cmbFavoritesGetPerPage;
    private JButton favoritesGetInvoke;
    private JTextArea favoritesGetOutput;
    private JCheckBox chkFavoritesGetLicense;
    private JCheckBox chkFavoritesGetOriginal;
    private JCheckBox chkFavoritesGetDateUp;
    private JCheckBox chkFavoritesGetLastUp;
    private JCheckBox chkFavoritesGetDateTak;
    private JCheckBox chkFavoritesGetGeo;
    private JCheckBox chkFavoritesGetTags;
    private JCheckBox chkFavoritesGetOwner;
    private JCheckBox chkFavoritesGetServer;
    private JCheckBox chkFavoritesGetMachine;
    private JTextField txtFavoritesGetPubID;
    private JTextField txtFavoritesGetPubPage;
    private JComboBox cmbFavoritesGetPubPerPage;
    private JButton favoritesGetPubInvoke;
    private JTextArea favoritesGetPubOutput;
    private JCheckBox chkFavoritesGetPubLicense;
    private JCheckBox chkFavoritesGetPubDateUp;
    private JCheckBox chkFavoritesGetPubDateTak;
    private JCheckBox chkFavoritesGetPubOwner;
    private JCheckBox chkFavoritesGetPubServer;
    private JCheckBox chkFavoritesGetPubOriginal;
    private JCheckBox chkFavoritesGetPubLastUp;
    private JCheckBox chkFavoritesGetPubGeo;
    private JCheckBox chkFavoritesGetPubTags;
    private JCheckBox chkFavoritesGetPubMachine;
    private JTextField txtFavoritesRmvID;
    private JButton favoritesRmvInvoke;
    private JTextArea favoritesRmvOutput;
    private JTabbedPane geoOperationPane;
    private JTextField txtGeoGetLocID;
    private JButton geoGetLocInvoke;
    private JTextArea geoGetLocOutput;
    private JTextField txtGeoGetPermsID;
    private JButton geoGetPermsInvoke;
    private JTextArea geoGetPermsOutput;
    private JTextField txtGeoRmvLocID;
    private JButton geoRmvLocInvoke;
    private JTextArea geoRmvLocOutput;
    private JTextField txtGeoSetLocID;
    private JComboBox cmbGeoSetLocLatitude;
    private JComboBox cmbGeoSetLocLongitude;
    private JComboBox cmbGeoSetLocAccuracy;
    private JButton geoSetLocInvoke;
    private JTextArea geoSetLocOutput;
    private JTextField txtGeoSetPermsID;
    private JButton geoSetPermsInvoke;
    private JTextArea geoSetPermsOutput;
    private JCheckBox chkGeoSetPermsPublic;
    private JCheckBox chkGeoSetPermsContact;
    private JCheckBox chkGeoSetPermsFriend;
    private JCheckBox chkGeoSetPermsFamily;
    private JTabbedPane groupsOperationPane;
    private JTextField txtGroupsBrowseID;
    private JButton groupsBrowseInvoke;
    private JTextArea groupsBrowseOutput;
    private JTextField txtGroupsGetInfoID;
    private JButton groupsGetInfoInvoke;
    private JTextArea groupsGetInfoOutput;
    private JTextField txtGroupsSearchText;
    private JTextField txtGroupsSearchPage;
    private JComboBox cmbGroupsSearchPerPage;
    private JButton groupsSearchInvoke;
    private JTextArea groupsSearchOutput;
    private JTabbedPane tabbedPane2;
    private JTextField txtGrpPoolsAddPhotoID;
    private JTextField txtGrpPoolsAddGroupID;
    private JButton grpPoolsAddInvoke;
    private JTextArea grpPoolsAddOutput;
    private JTextField txtGrpPoolsContextPhotoID;
    private JTextField txtGrpPoolsContextGrpID;
    private JButton grpPoolsContextInvoke;
    private JTextArea grpPoolsContextOutput;
    private JTextField txtGrpPoolsGrpsPage;
    private JComboBox cmbGrpPoolsGrpsPerPage;
    private JButton grpPoolsGrpsInvoke;
    private JTextArea grpPoolsGrpsOutput;
    private JButton blogsGetListInvoke;
    private JTextArea blogsGetListOutput;
    private FlickrServiceClient client = new FlickrServiceClient();
    private String key;
    private String host = "api.flickr.com";
    private String port = "443";
    private String token;
    private String sharedSecret;
    private String perms;

    public FlickrServiceFrame() {

        this.setTitle("WSO2 AppServer FlickrClient Sample - Invokes Flickr in a RESTfull manner");

        for (int i = 1; i <= 50; i++) {
            cmbGetContactsPhotos.addItem(new Integer(i));
            cmbGetContactsPublicPhotosCount.addItem(new Integer(i));
            cmbUserCommentsPerPage.addItem(new Integer(i));
            cmbUserPhotosPerPage.addItem(new Integer(i));
        }
        for (int i = 1; i <= 400; i++) {
            cmbGrpPoolsGrpsPerPage.addItem(new Integer(i));
        }

        for (int i = 1; i <= 500; i++) {
            cmbGetFavoritesPerPage.addItem(new Integer(i));
            cmbGetNotInSetPerPage.addItem(new Integer(i));
            cmbGetRecentPerPage.addItem(new Integer(i));
            cmbGetUntaggedPerPage.addItem(new Integer(i));
            cmbGetWithGeoDataPerPage.addItem(new Integer(i));
            cmbGetWithoutGeoDataPerPage.addItem(new Integer(i));
            cmbRecentlyUpdatedPerPage.addItem(new Integer(i));
            cmbSearchPerPage.addItem(new Integer(i));
            cmbFavoritesGetPerPage.addItem(new Integer(i));
            cmbFavoritesGetPubPerPage.addItem(new Integer(i));
            cmbGroupsSearchPerPage.addItem(new Integer(i));
        }

        for (int i = 1; i <= 1000; i++) {
            cmbContactsGetPerPage.addItem(new Integer(i));
            cmbContactsGetPubPerPage.addItem(new Integer(i));
        }

        for (int i = 1; i <= 16; i++) {
            cmbGeoSetLocAccuracy.addItem(new Integer(i));
            cmbSearchAccuracy.addItem(new Integer(i));
        }

        for (int i = -90; i <= 90; i++) {
            cmbGeoSetLocLatitude.addItem(new Integer(i));
        }

        for (int i = -180; i <= 180; i++) {
            cmbGeoSetLocLongitude.addItem(new Integer(i));
        }

        cmbGeoSetLocLatitude.setSelectedIndex(89);
        cmbGeoSetLocLongitude.setSelectedIndex(179);
        cmbGeoSetLocAccuracy.setSelectedIndex(15);
        cmbSearchAccuracy.setSelectedIndex(15);
        cmbUserCommentsPerPage.setSelectedIndex(9);
        cmbUserPhotosPerPage.setSelectedIndex(9);
        cmbGetContactsPhotos.setSelectedIndex(9);
        cmbGetContactsPublicPhotosCount.setSelectedIndex(9);
        cmbContactsGetPerPage.setSelectedIndex(999);
        cmbContactsGetPubPerPage.setSelectedIndex(999);
        cmbGetFavoritesPerPage.setSelectedIndex(9);
        cmbGetNotInSetPerPage.setSelectedIndex(99);
        cmbGetRecentPerPage.setSelectedIndex(99);
        cmbGetUntaggedPerPage.setSelectedIndex(99);
        cmbGetWithGeoDataPerPage.setSelectedIndex(99);
        cmbGetWithoutGeoDataPerPage.setSelectedIndex(99);
        cmbRecentlyUpdatedPerPage.setSelectedIndex(99);
        cmbSearchPerPage.setSelectedIndex(99);
        cmbFavoritesGetPerPage.setSelectedIndex(99);
        cmbFavoritesGetPubPerPage.setSelectedIndex(99);
        cmbGroupsSearchPerPage.setSelectedIndex(99);
        cmbGrpPoolsGrpsPerPage.setSelectedIndex(399);

        cmbContactsGetFilter.addItem("");
        cmbContactsGetFilter.addItem(Filter._both);
        cmbContactsGetFilter.addItem(Filter._family);
        cmbContactsGetFilter.addItem(Filter._friends);
        cmbContactsGetFilter.addItem(Filter._neither);

        cmbGetWithGeoDataPrivacy.addItem("");
        cmbGetWithGeoDataPrivacy.addItem(Filter._both);
        cmbGetWithGeoDataPrivacy.addItem(Filter._family);
        cmbGetWithGeoDataPrivacy.addItem(Filter._friends);
        cmbGetWithGeoDataPrivacy.addItem(Filter._neither);

        cmbGetWithoutGeoDataPrivacy.addItem("");
        cmbGetWithoutGeoDataPrivacy.addItem(Filter._both);
        cmbGetWithoutGeoDataPrivacy.addItem(Filter._family);
        cmbGetWithoutGeoDataPrivacy.addItem(Filter._friends);
        cmbGetWithoutGeoDataPrivacy.addItem(Filter._neither);

        JMenu configMenu = new JMenu("Configure");
        JMenuItem hostMenuItem = configMenu.add("Host");
        hostMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result =
                        JOptionPane.showInputDialog(getContentPane(), "Enter host address", host);
                if (result != null && !"".equals(result.trim())) {
                    host = result;
                }
            }
        });
        JMenuItem portMenuItem = configMenu.add("Port");
        portMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result = JOptionPane.showInputDialog(getContentPane(), "Enter port", port);
                if (result != null && !"".equals(result.trim())) {
                    port = result;
                }
            }
        });

        JMenuItem keyMenuItem = configMenu.add("API KEY");
        keyMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getAPIKEY();
            }
        });

        JMenuItem secretMenuItem = configMenu.add("Secret");
        secretMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getSharedSecret();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        menuBar.add(configMenu);

        // flickr.people Operations

        findByEmailInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPeopleEmail.getText().trim())) {
                    showRequiredMessage("E-mail");
                } else {
                    findByEmailOutput.setText(
                            client.flickrPeopleFindByEmail(txtPeopleEmail.getText(), key, host,
                                                           port));
                }
            }
        });

        findByUsernameInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPeopleUsername.getText().trim())) {
                    showRequiredMessage("Username");
                } else {
                    findByUsernameOutput
                            .setText(client.flickrPeopleFindByUsername(txtPeopleUsername.getText(),
                                                                       key, host, port));
                }
            }
        });

        getInfoInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPeopleGetInfo.getText().trim())) {
                    showRequiredMessage("User ID");
                } else {
                    getInfoOutput.setText(
                            client.flickrPeopleGetInfo(txtPeopleGetInfo.getText(), key, host,
                                                       port));
                }
            }
        });

        getPublicGroupsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetPublicGroups.getText().trim())) {
                    showRequiredMessage("User ID");
                } else {
                    getPublicGroupsOutput.setText(
                            client.flickrPeopleGetPublicGroups(txtGetPublicGroups.getText(),
                                                               key, host, port));
                }
            }
        });
        getPublicPhotosInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetPublicPhotos.getText().trim())) {
                    showRequiredMessage("User ID");
                } else {
                    getPublicPhotosOutput.setText(
                            client.flickrPeopleGetPublicPhotos(txtGetPublicPhotos.getText(),
                                                               key, host, port));
                }
            }
        });
        getUploadStatusInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {

                if (checkToken(READ))
                getUploadStatusOutput.setText(
                        client.flickrPeopleGetUploadStatus(sharedSecret, token,
                                                           key, host, port));
            }
        });

        photosGetInfoInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                String photoID = txtPhotosGetInfoPhotoID.getText();
                if ("".equals(photoID.trim())) {
                    showRequiredMessage("PhotoID");
                } else {
                    photosGetInfoOutput.setText(client.flickrPhotosGetInfo(photoID,
                                                                           txtPhotosGetInfoSecret.getText(),
                                                                           key, host, port));
                }
            }

        });

        // flickr.activity Operations

        userCommentsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ))
                userCommentsOutput.setText(client.flickrActivityUserComments(
                        txtUserCommentsPage.getText(),
                        cmbUserCommentsPerPage.getSelectedItem().toString(), sharedSecret, token,
                        key, host, port));
            }
        });

        userPhotosInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ))
                userPhotosOutput.setText(client.flickrActivityUserPhotos(
                        txtUserPhotosPage.getText(),
                        cmbUserPhotosPerPage.getSelectedItem().toString(),
                        txtUserPhotosTimeFrame.getText(), sharedSecret, token, key, host, port));
            }
        });

        // flickr.blog Operations

        blogsGetListInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ))
                blogsGetListOutput
                        .setText(client.flickrBlogsGetList(sharedSecret, token, key, host, port));
            }
        });

        postPhotoInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPostPhotoBlogID.getText().trim())) {
                    showRequiredMessage("Blog ID");
                } else if ("".equals(txtPostPhotoPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else if ("".equals(txtPostPhotoTitle.getText().trim())) {
                    showRequiredMessage("Title");
                } else if ("".equals(txtPostPhotoDescription.getText().trim())) {
                    showRequiredMessage("Description");
                } else {
                    if (checkToken(WRITE))
                    postPhotoOutput.setText(client.flickrBlogsPostPhoto(
                            txtPostPhotoBlogID.getText(), txtPostPhotoPhotoID.getText(),
                            txtPostPhotoTitle.getText(), txtPostPhotoDescription.getText(),
                            txtPostPhotoPassword.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        // flickr.photosets.comments operations

        photoSetsCommAddInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(PhotoSetsCommAddComment.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else if ("".equals(txtPhotoSetsCommAddID.getText().trim())) {
                    showRequiredMessage("Comment");
                } else {
                    if (checkToken(WRITE))
                    photoSetsCommAddOutput.setText(client.flickrPhotosetsCommentsAddComment(
                            PhotoSetsCommAddComment.getText(), txtPhotoSetsCommAddID.getText(),
                            sharedSecret, token, key, host, port));
                }
            }
        });

        photoSetsCommDelInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPhotoSetsCommDelID.getText().trim())) {
                    showRequiredMessage("Comment ID");
                } else {
                    if (checkToken(WRITE))
                    photoSetsCommDelOutput.setText(client.flickrPhotosetsCommentsDeleteComment(
                            txtPhotoSetsCommDelID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        photoSetsCommEditInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPhotoSetsCommEditID.getText().trim())) {
                    showRequiredMessage("Comment ID");
                } else if ("".equals(txtPhotoSetsCommEditComment.getText().trim())) {
                    showRequiredMessage("Comment");
                } else {
                    if (checkToken(WRITE))
                    PhotoSetsCommEditOutput.setText(client.flickrPhotosetsCommentsEditComment(
                            txtPhotoSetsCommEditComment.getText(), txtPhotoSetsCommEditID.getText(),
                            sharedSecret, token, key, host, port));
                }
            }
        });

        photoSetsCommGetInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPhotoSetsCommGetID.getText().trim())) {
                    showRequiredMessage("Photoset ID");
                } else {
                    photoSetsCommGetOutput.setText(client.flickrPhotosetsCommentsGetList(
                            txtPhotoSetsCommGetID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        // flickr.contacts Operations

        contactsGetInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                contactsGetOutput.setText(client.flickrContactsGetList(
                        cmbContactsGetFilter.getSelectedItem(), txtContactsGetPage.getText(),
                        cmbContactsGetPerPage.getSelectedItem().toString(), sharedSecret, token,
                        key, host, port));
            }
        });

        contactsGetPubInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtContactsGetPubID.getText().trim())) {
                    showRequiredMessage("User ID");
                } else {
                    contactsGetPubOutput.setText(client.flickrContactsGetPublicList(
                            txtContactsGetPubID.getText(), txtContactsGetPubPage.getText(),
                            cmbContactsGetPubPerPage.getSelectedItem().toString(), sharedSecret,
                            token, key, host, port));
                }
            }
        });

        // flickr.favorites Operations

        favoritesAddInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtFavoritesAddID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(WRITE))
                    favoritesAddOutput.setText(client.flickrFavoritesAdd(
                            txtFavoritesAddID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        favoritesGetInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkFavoritesGetLicense.isSelected());
                extrasBean.setDate_taken(chkFavoritesGetDateTak.isSelected());
                extrasBean.setDate_upload(chkFavoritesGetDateUp.isSelected());
                extrasBean.setGeo(chkFavoritesGetGeo.isSelected());
                extrasBean.setIcon_server(chkFavoritesGetServer.isSelected());
                extrasBean.setLast_update(chkFavoritesGetLastUp.isSelected());
                extrasBean.setMachine_tags(chkFavoritesGetMachine.isSelected());
                extrasBean.setOriginal_format(chkFavoritesGetOriginal.isSelected());
                extrasBean.setOwner_name(chkFavoritesGetOwner.isSelected());
                extrasBean.setTags(chkFavoritesGetTags.isSelected());
                favoritesGetOutput.setText(client.flickrFavoritesGetList(
                        txtFavoritesGetID.getText(), txtFavoritesGetPage.getText(),
                        cmbFavoritesGetPerPage.getSelectedItem().toString(), extrasBean,
                        sharedSecret, token, key, host, port));
                }
            }
        });

        favoritesGetPubInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtFavoritesGetPubID.getText().trim())) {
                    showRequiredMessage("User ID");
                } else {
                    ExtrasBean extrasBean = new ExtrasBean();
                    extrasBean.setLicense(chkFavoritesGetPubLicense.isSelected());
                    extrasBean.setDate_taken(chkFavoritesGetPubDateTak.isSelected());
                    extrasBean.setDate_upload(chkFavoritesGetPubDateUp.isSelected());
                    extrasBean.setGeo(chkFavoritesGetPubGeo.isSelected());
                    extrasBean.setIcon_server(chkFavoritesGetPubServer.isSelected());
                    extrasBean.setLast_update(chkFavoritesGetPubLastUp.isSelected());
                    extrasBean.setMachine_tags(chkFavoritesGetPubMachine.isSelected());
                    extrasBean.setOriginal_format(chkFavoritesGetPubOriginal.isSelected());
                    extrasBean.setOwner_name(chkFavoritesGetPubOwner.isSelected());
                    extrasBean.setTags(chkFavoritesGetPubTags.isSelected());
                    favoritesGetPubOutput.setText(client.flickrFavoritesGetPublicList(
                            txtFavoritesGetPubID.getText(), txtFavoritesGetPubPage.getText(),
                            cmbFavoritesGetPubPerPage.getSelectedItem().toString(), extrasBean,
                            sharedSecret, token, key, host, port));
                }
            }
        });

        favoritesRmvInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtFavoritesRmvID.getText().trim())) {
                    showRequiredMessage("User ID");
                } else {
                    if (checkToken(WRITE))
                    favoritesRmvOutput.setText(client.flickrFavoritesRemove(
                            txtFavoritesRmvID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        // flickr.photos.geo Operations

        geoGetLocInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGeoGetLocID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    geoGetLocOutput.setText(client.flickrPhotosGeoGetLocation(
                            txtGeoGetLocID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        geoGetPermsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGeoGetPermsID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(READ))
                    geoGetPermsOutput.setText(client.flickrPhotosGeoGetPerms(
                            txtGeoGetPermsID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        geoRmvLocInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGeoRmvLocID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(WRITE))
                    geoRmvLocOutput.setText(client.flickrPhotosGeoRemoveLocation(
                            txtGeoRmvLocID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });


        geoSetPermsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGeoSetPermsID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(WRITE))
                    geoSetPermsOutput.setText(client.flickrPhotosGeoSetPerms(
                            txtGeoSetPermsID.getText(), chkGeoSetPermsPublic.isSelected(),
                            chkGeoSetPermsContact.isSelected(), chkGeoSetPermsFriend.isSelected(),
                            chkGeoSetPermsFamily.isSelected(), sharedSecret, token, key, host,
                            port));
                }
            }
        });

        // flickr.groups Operations

        groupsBrowseInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ))
                groupsBrowseOutput.setText(client.flickrGroupsBrowse(txtGroupsBrowseID.getText(),
                                                                     sharedSecret, token, key, host,
                                                                     port));
            }
        });

        groupsGetInfoInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGroupsGetInfoID.getText().trim())) {
                    showRequiredMessage("Group ID");
                } else {
                    groupsGetInfoOutput.setText(client.flickrGroupsGetInfo(
                            txtGroupsGetInfoID.getText(), sharedSecret, token, key, host, port));
                }
            }
        });

        groupsSearchInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGroupsSearchText.getText().trim())) {
                    showRequiredMessage("Text");
                } else {
                    if (checkToken(WRITE))
                    groupsSearchOutput.setText(client.flickrGroupsSearch(
                            txtGroupsSearchText.getText(), txtGroupsSearchPage.getText(),
                            cmbGroupsSearchPerPage.getSelectedItem().toString(), sharedSecret,
                            token, key, host, port));
                }
            }
        });

        // flickr.groups.pools Operations

        grpPoolsAddInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGrpPoolsAddPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else if ("".equals(txtGrpPoolsAddGroupID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    grpPoolsAddOutput.setText(client.flickrGroupsPoolsAdd(
                            txtGrpPoolsAddPhotoID.getText(), txtGrpPoolsAddGroupID.getText(),
                            sharedSecret, token, key, host, port));
                }
            }
        });

        grpPoolsContextInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGrpPoolsContextPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else if ("".equals(txtGrpPoolsContextGrpID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    grpPoolsContextOutput.setText(client.flickrGroupsPoolsGetContext(
                            txtGrpPoolsContextPhotoID.getText(), txtGrpPoolsContextGrpID.getText(),
                            sharedSecret, token, key, host, port));
                }
            }
        });

        grpPoolsGrpsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ))
                grpPoolsGrpsOutput.setText(client.flickrGroupsPoolsGetGroups(
                        txtGrpPoolsGrpsPage.getText(),
                        cmbGrpPoolsGrpsPerPage.getSelectedItem().toString(), sharedSecret, token,
                        key, host, port));
            }
        });

        // flickr.photos Operations

        photosGetInfoInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPhotosGetInfoPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    photosGetInfoOutput.setText(client.flickrPhotosGetInfo(
                            txtPhotosGetInfoPhotoID.getText(), sharedSecret,
                            key, host, port));
                }
            }
        });

        photosAddTagsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPhotosAddTagsPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else if ("".equals(txtAddTags.getText().trim())) {
                    showRequiredMessage("Tags");
                } else {
                    if (checkToken(WRITE))
                    photosAddTagsOutput.setText(client.flickrPhotosAddTags(
                            txtPhotosAddTagsPhotoID.getText(), txtAddTags.getText(), sharedSecret,
                            token,
                            key, host, port));
                }
            }
        });

        photosDeleteInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtPhotosDeletePhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(DELETE))
                    photosDeleteOutput.setText(client.flickrPhotosDelete(
                            txtPhotosDeletePhotoID.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        getAllContextsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetAllContextsPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(DELETE))
                    getAllContextsOutput.setText(client.flickrPhotosGetAllContexts(
                            txtGetAllContextsPhotoID.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        getContactsPhotosInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetContactsPhotosLicense.isSelected());
                extrasBean.setDate_taken(chkGetContactsPhotosDateTaken.isSelected());
                extrasBean.setDate_upload(chkGetContactsPhotosUploadDate.isSelected());
                extrasBean.setIcon_server(chkGetContactsPhotosServer.isSelected());
                extrasBean.setLast_update(chkGetContactsPhotosLastUpdate.isSelected());
                extrasBean.setOriginal_format(chkGetContactsPhotosOriginal.isSelected());
                extrasBean.setOwner_name(chkGetContactsPhotosOwner.isSelected());

                getContactsPhotosOutput.setText(client.flickrPhotosGetContactsPhotos(
                        cmbGetContactsPhotos.getSelectedItem().toString(),
                        chkGetContactsPhotosFriends.isSelected(),
                        chkGetContactsPhotosSingle.isSelected(),
                        chkGetContactsPhotosSelf.isSelected(), extrasBean, sharedSecret, token,
                        key, host, port));
                }
            }
        });

        getContactsPublicPhotosInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)){
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetContactsPhotosLicense.isSelected());
                extrasBean.setDate_taken(chkGetContactsPhotosDateTaken.isSelected());
                extrasBean.setDate_upload(chkGetContactsPhotosUploadDate.isSelected());
                extrasBean.setIcon_server(chkGetContactsPhotosServer.isSelected());
                extrasBean.setLast_update(chkGetContactsPhotosLastUpdate.isSelected());
                extrasBean.setOriginal_format(chkGetContactsPhotosOriginal.isSelected());
                extrasBean.setOwner_name(chkGetContactsPhotosOwner.isSelected());

                getContactsPublicPhotosOutput.setText(client.flickrPhotosGetContactsPublicPhotos(
                        txtGetContactsPublicPhotosUserID.getText(),
                        cmbGetContactsPublicPhotosCount.getSelectedItem().toString(),
                        chkGetContactsPublicPhotosFriends.isSelected(),
                        chkGetContactsPublicPhotosSingle.isSelected(),
                        chkGetContactsPublicPhotosSelf.isSelected(), extrasBean, sharedSecret,
                        token,
                        key, host, port));
                }
            }
        });

        getContextInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetContextPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    getContextOutput.setText(client.flickrPhotosGetContext(
                            txtGetContextPhotoID.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        getCountsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ))
                getCountsOutput.setText(client.flickrPhotosGetCounts(
                        txtGetCountsDates.getText(), txtGetCountsDatesTaken.getText(), sharedSecret,
                        token,
                        key, host, port));
            }
        });

        getExifInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetExifPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    getExifOutput.setText(client.flickrPhotosGetExif(
                            txtGetExifPhotoID.getText(), txtGetExifSecret.getText(), sharedSecret,
                            token,
                            key, host, port));
                }
            }
        });

        getFavoritesInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetFavoritesPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    getFavoritesOutput.setText(client.flickrPhotosGetFavorites(
                            txtGetFavoritesPhotoID.getText(), txtGetFavoritesPage.getText(),
                            cmbGetFavoritesPerPage.getSelectedItem().toString(), sharedSecret,
                            token,
                            key, host, port));
                }
            }
        });

        getNotInSetInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetNotInSetLicense.isSelected());
                extrasBean.setDate_taken(chkGetNotInSetDateTak.isSelected());
                extrasBean.setDate_upload(chkGetNotInSetDateUp.isSelected());
                extrasBean.setGeo(chkGetNotInSetGeo.isSelected());
                extrasBean.setIcon_server(chkGetNotInSetServer.isSelected());
                extrasBean.setLast_update(chkGetNotInSetLastUp.isSelected());
                extrasBean.setMachine_tags(chkGetNotInSetMachine.isSelected());
                extrasBean.setOriginal_format(chkGetNotInSetOriginal.isSelected());
                extrasBean.setOwner_name(chkGetNotInSetOwner.isSelected());
                extrasBean.setTags(chkGetNotInSetTags.isSelected());
                getNotInSetOutput.setText(client.flickrPhotosGetNotInSet(
                        txtGetNotInSetMinUpDate.getText(), txtGetNotInSetMaxUpDate.getText(),
                        txtGetNotInSetMinTakDate.getText(), txtGetNotInSetMaxTakDate.getText(),
                        cmbGetNotInSetPrivacy.getSelectedItem().toString(), extrasBean,
                        txtGetNotInSetPage.getText(),
                        cmbGetNotInSetPerPage.getSelectedItem().toString(),
                        sharedSecret, token, key, host, port));
                }
            }
        });

        getPermsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetPermsPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(READ))
                    getPermsOutput.setText(client.flickrPhotosGetPerms(
                            txtGetPermsPhotoID.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        getRecentInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetRecentLicense.isSelected());
                extrasBean.setDate_taken(chkGetRecentDateTak.isSelected());
                extrasBean.setDate_upload(chkGetRecentDateUp.isSelected());
                extrasBean.setGeo(chkGetRecentGeo.isSelected());
                extrasBean.setIcon_server(chkGetRecentServer.isSelected());
                extrasBean.setLast_update(chkGetRecentLastUp.isSelected());
                extrasBean.setMachine_tags(chkGetRecentMachine.isSelected());
                extrasBean.setOriginal_format(chkGetRecentOriginal.isSelected());
                extrasBean.setOwner_name(chkGetRecentOwner.isSelected());
                extrasBean.setTags(chkGetRecentTags.isSelected());
                getRecentOutput.setText(
                        client.flickrPhotosGetRecent(extrasBean, txtGetRecentPage.getText(),
                                                     cmbGetRecentPerPage
                                                             .getSelectedItem().toString(),
                                                     sharedSecret, token, key, host, port));
            }
        });

        getSizesInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtGetSizesPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    getSizesOutput.setText(client.flickrPhotosGetSizes(
                            txtGetSizesPhotoID.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        getUntaggedInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetUntaggedLicense.isSelected());
                extrasBean.setDate_taken(chkGetUntaggedDateTak.isSelected());
                extrasBean.setDate_upload(chkGetUntaggedDateUp.isSelected());
                extrasBean.setGeo(chkGetUntaggedGeo.isSelected());
                extrasBean.setIcon_server(chkGetUntaggedServer.isSelected());
                extrasBean.setLast_update(chkGetUntaggedLastUp.isSelected());
                extrasBean.setMachine_tags(chkGetUntaggedMachine.isSelected());
                extrasBean.setOriginal_format(chkGetUntaggedOriginal.isSelected());
                extrasBean.setOwner_name(chkGetUntaggedOwner.isSelected());
                extrasBean.setTags(chkGetUntaggedTags.isSelected());
                getUntaggedOutput.setText(client.flickrPhotosGetUntagged(
                        txtGetUntaggedMinUpDate.getText(), txtGetUntaggedMaxUpDate.getText(),
                        txtGetUntaggedMinTakDate.getText(), txtGetUntaggedMaxTakDate.getText(),
                        cmbGetUntaggedPrivacy.getSelectedItem().toString(), extrasBean,
                        txtGetUntaggedPage.getText(),
                        cmbGetUntaggedPerPage.getSelectedItem().toString(),
                        sharedSecret, token, key, host, port));
                }
            }
        });

        getWithGeoDataInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetWithGeoDataLicense.isSelected());
                extrasBean.setDate_taken(chkGetWithGeoDataDateTak.isSelected());
                extrasBean.setDate_upload(chkGetWithGeoDataDateUp.isSelected());
                extrasBean.setGeo(chkGetWithGeoDataGeo.isSelected());
                extrasBean.setIcon_server(chkGetWithGeoDataServer.isSelected());
                extrasBean.setLast_update(chkGetWithGeoDataLastUp.isSelected());
                extrasBean.setMachine_tags(chkGetWithGeoDataMachine.isSelected());
                extrasBean.setOriginal_format(chkGetWithGeoDataOriginal.isSelected());
                extrasBean.setOwner_name(chkGetWithGeoDataOwner.isSelected());
                extrasBean.setTags(chkGetWithGeoDataTags.isSelected());
                getWithGeoDataOutput.setText(client.flickrPhotosGetWithGeoData(
                        txtGetWithGeoDataMinUpDate.getText(), txtGetWithGeoDataMaxUpDate.getText(),
                        txtGetWithGeoDataMinTakDate.getText(),
                        txtGetWithGeoDataMaxTakDate.getText(),
                        cmbGetWithGeoDataPrivacy.getSelectedItem().toString(), extrasBean,
                        txtGetWithGeoDataPage.getText(),
                        cmbGetWithGeoDataPerPage.getSelectedItem().toString(),
                        sharedSecret, token, key, host, port));
                }
            }
        });

        getWithoutGeoDataInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkGetWithoutGeoDataLicense.isSelected());
                extrasBean.setDate_taken(chkGetWithoutGeoDataDateTak.isSelected());
                extrasBean.setDate_upload(chkGetWithoutGeoDataDateUp.isSelected());
                extrasBean.setGeo(chkGetWithoutGeoDataGeo.isSelected());
                extrasBean.setIcon_server(chkGetWithoutGeoDataServer.isSelected());
                extrasBean.setLast_update(chkGetWithoutGeoDataLastUp.isSelected());
                extrasBean.setMachine_tags(chkGetWithoutGeoDataMachine.isSelected());
                extrasBean.setOriginal_format(chkGetWithoutGeoDataOriginal.isSelected());
                extrasBean.setOwner_name(chkGetWithoutGeoDataOwner.isSelected());
                extrasBean.setTags(chkGetWithoutGeoDataTags.isSelected());
                getWithoutGeoDataOutput.setText(client.flickrPhotosGetWithoutGeoData(
                        txtGetWithoutGeoDataMinUpDate.getText(),
                        txtGetWithoutGeoDataMaxUpDate.getText(),
                        txtGetWithoutGeoDataMinTakDate.getText(),
                        txtGetWithoutGeoDataMaxTakDate.getText(),
                        cmbGetWithoutGeoDataPrivacy.getSelectedItem().toString(), extrasBean,
                        txtGetWithoutGeoDataPage.getText(),
                        cmbGetWithoutGeoDataPerPage.getSelectedItem().toString(),
                        sharedSecret, token, key, host, port));
                }
            }
        });

        recentlyUpdatedInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if (checkToken(READ)) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkRecentlyUpdatedLicense.isSelected());
                extrasBean.setDate_taken(chkRecentlyUpdatedDateTak.isSelected());
                extrasBean.setDate_upload(chkRecentlyUpdatedDateUp.isSelected());
                extrasBean.setGeo(chkRecentlyUpdatedGeo.isSelected());
                extrasBean.setIcon_server(chkRecentlyUpdatedServer.isSelected());
                extrasBean.setLast_update(chkRecentlyUpdatedLastUp.isSelected());
                extrasBean.setMachine_tags(chkRecentlyUpdatedMachine.isSelected());
                extrasBean.setOriginal_format(chkRecentlyUpdatedOriginal.isSelected());
                extrasBean.setOwner_name(chkRecentlyUpdatedOwner.isSelected());
                extrasBean.setTags(chkRecentlyUpdatedTags.isSelected());
                recentlyUpdatedOutput.setText(client.flickrPhotosRecentlyUpdated(
                        txtRecentlyUpdatedMinDate.getText(), extrasBean,
                        txtRecentlyUpdatedPage.getText(),
                        cmbRecentlyUpdatedPerPage.getSelectedItem().toString(),
                        sharedSecret, token, key, host, port));
                }
            }
        });

        removeTagInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtRemoveTagTadID.getText().trim())) {
                    showRequiredMessage("Tag ID");
                } else {
                    removeTagOutput.setText(client.flickrPhotosRemoveTag(
                            txtRemoveTagTadID.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        searchInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                ExtrasBean extrasBean = new ExtrasBean();
                extrasBean.setLicense(chkSearchLicense.isSelected());
                extrasBean.setDate_taken(chkSearchDateTak.isSelected());
                extrasBean.setDate_upload(chkSearchDateUp.isSelected());
                extrasBean.setGeo(chkSearchGeo.isSelected());
                extrasBean.setIcon_server(chkSearchServer.isSelected());
                extrasBean.setLast_update(chkSearchLastUp.isSelected());
                extrasBean.setMachine_tags(chkSearchMachine.isSelected());
                extrasBean.setOriginal_format(chkSearchOriginal.isSelected());
                extrasBean.setOwner_name(chkSearchOwner.isSelected());
                extrasBean.setTags(chkSearchTags.isSelected());
                searchOutput.setText(client.flickrPhotosSearch( txtSearchUserID.getText(), txtSearchTags.getText(), (AnyOrAll)cmbSearchTagMode.getSelectedItem(), txtSearchText.getText(),
                        txtSearchMinUpDate.getText(), txtSearchMaxUpDate.getText(), txtSearchMinTakDate.getText(), txtMaxTakDate.getText(), txtSearchLicense.getText(), (SortOrder)cmbSearchSort.getSelectedItem(), cmbSearchPrivacy.getSelectedItem().toString(), cmbSearchAccuracy.getSelectedItem().toString(), txtSearchMachine.getText(), (AnyOrAll)cmbSearchMachineMode.getSelectedItem(), txtSearchGroupID.getText(), extrasBean, 
                        txtSearchPage.getText(),
                        cmbSearchPerPage.getSelectedItem().toString(),
                        sharedSecret, token, key, host, port));
            }
        });

        setDatesInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtSetDatesPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(WRITE))
                    setDatesOutput.setText(client.flickrPhotosSetDates(
                            txtSetDatesPhotoID.getText(), txtSetDatesPosted.getText(), txtSetDatesTaken.getText(), txtSetDatesGranularity.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        setMetaInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtSetMetaPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else if ("".equals(txtSetMetaTitle.getText().trim())) {
                    showRequiredMessage("Title");
                } else if ("".equals(txtSetMetaDescription.getText().trim())) {
                    showRequiredMessage("Description");
                } else {
                    if (checkToken(WRITE))
                    setMetaOutput.setText(client.flickrPhotosSetMeta(
                            txtSetMetaPhotoID.getText(), txtSetMetaTitle.getText(), txtSetMetaDescription.getText(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

        setPermsInvoke.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             */
            public void actionPerformed(ActionEvent e) {
                if ("".equals(txtSetPermsPhotoID.getText().trim())) {
                    showRequiredMessage("Photo ID");
                } else {
                    if (checkToken(WRITE))
                    setPermsOutput.setText(client.flickrPhotosSetPerms(
                            txtSetPermsPhotoID.getText(), chkSetPermsPublic.isSelected(), chkSetPermsFriends.isSelected(), chkSetPermsFamily.isSelected(), cmbSetPermsComments.getSelectedItem().toString(), cmbSetPermsMeta.getSelectedItem().toString(), sharedSecret, token,
                            key, host, port));
                }
            }
        });

    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }


    private boolean checkToken(String permNedded) {
        if (this.sharedSecret == null) {
            if (!getSharedSecret()) {
                return false;
            }
        }
        if (this.token == null || !hasPerms(permNedded)) {
            String frob;
            int result = JOptionPane.showConfirmDialog(this,
                                                       "<html><h2>This Program Requires authorization <br></br>before it can read or modify your photos <br></br>and data on Flickr.</h2>" +
                                                               "<p>Authorization is a simple process which takes place in your <br></br>" +
                                                               "web browser. When your finished return to this window <br></br>" +
                                                               "to complete authorization and begin using FlickrClient.</p>" +
                                                               "<p>(You must be connected to the internet in order to authorize<br></br>this program.)</p></html>",
                                                       "Flickr API-Based FlickrClient",
                                                       JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    frob = client.flickrAuthGetFrob(key, host, port, sharedSecret);
                    String sig = DigestUtils.md5Hex(
                            sharedSecret + "api_key" + key + "frob" + frob + "perms" + permNedded);
                    openURL("http://flickr.com/services/auth/?api_key=" + key + "&perms=" +
                            permNedded + "&frob=" + frob + "&api_sig=" + sig);
                    result = JOptionPane.showConfirmDialog(this,
                                                           "<html><h2>Return to this window after you have <br></br>" +
                                                                   "finished the authorization process on <br></br>" +
                                                                   "Flickr.com</h2>" +
                                                                   "<p>Once you're done, click the 'ok' button <br></br>" +
                                                                   "below and you can begin using FlickrClient.</p>" +
                                                                   "<p>(You can revoke this program's authorization at any time in your " +
                                                                   "account page on Flickr.com.)</p></html>",
                                                           "Flickr API-Based FlickrClient",
                                                           JOptionPane.INFORMATION_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        token = client.flickrAuthGetToken(key, host, port, sharedSecret, frob);
                        return true;
                    }
                } catch (Exception e) {
                    //todo
                    e.printStackTrace();
                }
            }
            return false;
        }
        return true;
    }

    private boolean hasPerms(String permNedded) {

        if (DELETE.equals(permNedded)) {
            if (DELETE.equals(perms)) {
                return true;
            }
        } else if (WRITE.equals(permNedded)) {
            if (READ.equals(perms)) {
                return false;
            }
        }
        return true;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void getAPIKEY() {
        while (true) {
            key = JOptionPane.showInputDialog(this, "Please enter a valid API KEY for flickr",
                                              "Enter API KEY",
                                              JOptionPane.INFORMATION_MESSAGE);
            if (key == null) {
                System.exit(0);
            } else if ("".equals(key.trim())) {
                JOptionPane.showMessageDialog(this, "The API KEY cannot be empty");
            } else {
                break;
            }
        }
        this.setSharedSecret(sharedSecret);
    }

    public boolean getSharedSecret() {
        while (true) {
            sharedSecret = JOptionPane.showInputDialog(this,
                                                       "Please enter the shared secret corresponding to your API KEY",
                                                       "Enter Shared Secret",
                                                       JOptionPane.INFORMATION_MESSAGE);
            if (sharedSecret == null) {
                return false;
            }
            else if ("".equals(sharedSecret.trim())) {
                JOptionPane.showMessageDialog(this, "The Shared Secret cannot be empty");
            } else {
                break;
            }
        }
        this.setKey(key);
        return true;
    }

    private void showRequiredMessage(String param) {
        JOptionPane.showMessageDialog(this, "Parameter " + param + " is mandatory");
    }

    private void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else { //assume Unix or Linux
                String[] browsers =
                        {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]})
                            .waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error attempting to launch web browser" + ":\n" +
                    e.getLocalizedMessage());
        }
    }


    {
        setupUI();
    }

    private void setupUI() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMaximumSize(new Dimension(900, 700));
        panel1.setMinimumSize(new Dimension(900, 700));
        panel1.setPreferredSize(new Dimension(900, 700));
        panel1.setRequestFocusEnabled(false);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setPreferredSize(new Dimension(900, 700));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(tabbedPane1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        tabbedPane1.addTab("People", panel2);
        peopleOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(peopleOperationPane, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        peopleOperationPane.addTab("FindByEmail", panel3);
        final JLabel label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(0);
        label1.setMaximumSize(new Dimension(400, 50));
        label1.setMinimumSize(new Dimension(400, 50));
        label1.setPreferredSize(new Dimension(400, 50));
        label1.setText("Return a user's NSID, given their email address");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel3.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.BOLD, label2.getFont().getSize()));
        label2.setHorizontalAlignment(4);
        label2.setMaximumSize(new Dimension(400, 25));
        label2.setMinimumSize(new Dimension(400, 25));
        label2.setPreferredSize(new Dimension(400, 25));
        label2.setText("E-Mail :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label2, gbc);
        txtPeopleEmail = new JTextField();
        txtPeopleEmail.setEditable(true);
        txtPeopleEmail.setMaximumSize(new Dimension(200, 25));
        txtPeopleEmail.setMinimumSize(new Dimension(200, 25));
        txtPeopleEmail.setPreferredSize(new Dimension(200, 25));
        txtPeopleEmail.setRequestFocusEnabled(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel3.add(txtPeopleEmail, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBackground(new Color(-3355444));
        scrollPane1.setMaximumSize(new Dimension(550, 225));
        scrollPane1.setMinimumSize(new Dimension(550, 225));
        scrollPane1.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel3.add(scrollPane1, gbc);
        findByEmailOutput = new JTextArea();
        findByEmailOutput.setBackground(new Color(-3355444));
        findByEmailOutput.setEditable(false);
        findByEmailOutput.setText("");
        scrollPane1.setViewportView(findByEmailOutput);
        findByEmailInvoke = new JButton();
        findByEmailInvoke.setFocusCycleRoot(true);
        findByEmailInvoke.setFont(new Font(findByEmailInvoke.getFont().getName(), Font.BOLD,
                                           findByEmailInvoke.getFont().getSize()));
        findByEmailInvoke.setMaximumSize(new Dimension(100, 30));
        findByEmailInvoke.setMinimumSize(new Dimension(100, 30));
        findByEmailInvoke.setPreferredSize(new Dimension(100, 30));
        findByEmailInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel3.add(findByEmailInvoke, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        peopleOperationPane.addTab("FindByUsername", panel4);
        final JLabel label3 = new JLabel();
        label3.setFont(new Font(label3.getFont().getName(), Font.BOLD, label3.getFont().getSize()));
        label3.setHorizontalAlignment(0);
        label3.setHorizontalTextPosition(0);
        label3.setMaximumSize(new Dimension(400, 50));
        label3.setMinimumSize(new Dimension(400, 50));
        label3.setPreferredSize(new Dimension(400, 50));
        label3.setText("Return a user's NSID, given their username.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel4.add(label3, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(panel5, gbc);
        findByUsernameInvoke = new JButton();
        findByUsernameInvoke.setFont(new Font(findByUsernameInvoke.getFont().getName(), Font.BOLD,
                                              findByUsernameInvoke.getFont().getSize()));
        findByUsernameInvoke.setMaximumSize(new Dimension(100, 30));
        findByUsernameInvoke.setMinimumSize(new Dimension(100, 30));
        findByUsernameInvoke.setPreferredSize(new Dimension(100, 30));
        findByUsernameInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel5.add(findByUsernameInvoke, gbc);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBackground(new Color(-3355444));
        scrollPane2.setMaximumSize(new Dimension(550, 225));
        scrollPane2.setMinimumSize(new Dimension(550, 225));
        scrollPane2.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel5.add(scrollPane2, gbc);
        findByUsernameOutput = new JEditorPane();
        findByUsernameOutput.setBackground(new Color(-3355444));
        findByUsernameOutput.setEditable(false);
        findByUsernameOutput.setForeground(new Color(-16777216));
        findByUsernameOutput.setText("");
        scrollPane2.setViewportView(findByUsernameOutput);
        final JLabel label4 = new JLabel();
        label4.setFont(new Font(label4.getFont().getName(), Font.BOLD, label4.getFont().getSize()));
        label4.setHorizontalAlignment(4);
        label4.setMaximumSize(new Dimension(400, 25));
        label4.setMinimumSize(new Dimension(400, 25));
        label4.setPreferredSize(new Dimension(400, 25));
        label4.setText("Username :");
        label4.setVerifyInputWhenFocusTarget(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel4.add(label4, gbc);
        txtPeopleUsername = new JTextField();
        txtPeopleUsername.setMaximumSize(new Dimension(200, 25));
        txtPeopleUsername.setMinimumSize(new Dimension(200, 25));
        txtPeopleUsername.setOpaque(true);
        txtPeopleUsername.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel4.add(txtPeopleUsername, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        peopleOperationPane.addTab("GetInfo", panel6);
        final JLabel label5 = new JLabel();
        label5.setFont(new Font(label5.getFont().getName(), Font.BOLD, label5.getFont().getSize()));
        label5.setHorizontalAlignment(0);
        label5.setHorizontalTextPosition(0);
        label5.setMaximumSize(new Dimension(400, 50));
        label5.setMinimumSize(new Dimension(400, 50));
        label5.setPreferredSize(new Dimension(400, 50));
        label5.setText("Get information about a user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel6.add(label5, gbc);
        txtUserID = new JLabel();
        txtUserID.setFont(
                new Font(txtUserID.getFont().getName(), Font.BOLD, txtUserID.getFont().getSize()));
        txtUserID.setHorizontalAlignment(4);
        txtUserID.setMaximumSize(new Dimension(400, 25));
        txtUserID.setMinimumSize(new Dimension(400, 25));
        txtUserID.setPreferredSize(new Dimension(400, 25));
        txtUserID.setText("User ID :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel6.add(txtUserID, gbc);
        txtPeopleGetInfo = new JTextField();
        txtPeopleGetInfo.setMaximumSize(new Dimension(200, 25));
        txtPeopleGetInfo.setMinimumSize(new Dimension(200, 25));
        txtPeopleGetInfo.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel6.add(txtPeopleGetInfo, gbc);
        getInfoInvoke = new JButton();
        getInfoInvoke.setFont(new Font(getInfoInvoke.getFont().getName(), Font.BOLD,
                                       getInfoInvoke.getFont().getSize()));
        getInfoInvoke.setMaximumSize(new Dimension(100, 30));
        getInfoInvoke.setMinimumSize(new Dimension(100, 30));
        getInfoInvoke.setPreferredSize(new Dimension(100, 30));
        getInfoInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel6.add(getInfoInvoke, gbc);
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setHorizontalScrollBarPolicy(30);
        scrollPane3.setMaximumSize(new Dimension(550, 225));
        scrollPane3.setMinimumSize(new Dimension(550, 225));
        scrollPane3.setPreferredSize(new Dimension(550, 225));
        scrollPane3.setVerticalScrollBarPolicy(20);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel6.add(scrollPane3, gbc);
        getInfoOutput = new JTextArea();
        getInfoOutput.setAutoscrolls(false);
        getInfoOutput.setBackground(new Color(-3355444));
        getInfoOutput.setEditable(false);
        scrollPane3.setViewportView(getInfoOutput);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        peopleOperationPane.addTab("GetPublicGroups", panel7);
        final JLabel label6 = new JLabel();
        label6.setFont(new Font(label6.getFont().getName(), Font.BOLD, label6.getFont().getSize()));
        label6.setHorizontalAlignment(0);
        label6.setHorizontalTextPosition(0);
        label6.setMaximumSize(new Dimension(400, 50));
        label6.setMinimumSize(new Dimension(400, 50));
        label6.setPreferredSize(new Dimension(400, 50));
        label6.setText("Returns the list of public groups a user is a member of.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel7.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setFont(new Font(label7.getFont().getName(), Font.BOLD, label7.getFont().getSize()));
        label7.setHorizontalAlignment(4);
        label7.setMaximumSize(new Dimension(400, 25));
        label7.setMinimumSize(new Dimension(400, 25));
        label7.setPreferredSize(new Dimension(400, 25));
        label7.setText("User ID :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel7.add(label7, gbc);
        txtGetPublicGroups = new JTextField();
        txtGetPublicGroups.setMaximumSize(new Dimension(200, 25));
        txtGetPublicGroups.setMinimumSize(new Dimension(200, 25));
        txtGetPublicGroups.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel7.add(txtGetPublicGroups, gbc);
        getPublicGroupsInvoke = new JButton();
        getPublicGroupsInvoke.setFont(new Font(getPublicGroupsInvoke.getFont().getName(), Font.BOLD,
                                               getPublicGroupsInvoke.getFont().getSize()));
        getPublicGroupsInvoke.setLabel("Invoke");
        getPublicGroupsInvoke.setMaximumSize(new Dimension(100, 30));
        getPublicGroupsInvoke.setMinimumSize(new Dimension(100, 30));
        getPublicGroupsInvoke.setPreferredSize(new Dimension(100, 30));
        getPublicGroupsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel7.add(getPublicGroupsInvoke, gbc);
        final JScrollPane scrollPane4 = new JScrollPane();
        scrollPane4.setMaximumSize(new Dimension(550, 225));
        scrollPane4.setMinimumSize(new Dimension(550, 225));
        scrollPane4.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel7.add(scrollPane4, gbc);
        getPublicGroupsOutput = new JTextArea();
        getPublicGroupsOutput.setBackground(new Color(-3355444));
        getPublicGroupsOutput.setEditable(false);
        scrollPane4.setViewportView(getPublicGroupsOutput);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        peopleOperationPane.addTab("GetPublicPhotos", panel8);
        final JLabel label8 = new JLabel();
        label8.setFont(new Font(label8.getFont().getName(), Font.BOLD, label8.getFont().getSize()));
        label8.setHorizontalAlignment(0);
        label8.setMaximumSize(new Dimension(400, 50));
        label8.setMinimumSize(new Dimension(400, 50));
        label8.setPreferredSize(new Dimension(400, 50));
        label8.setText("Get a list of public photos for the given user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel8.add(label8, gbc);
        final JLabel label9 = new JLabel();
        label9.setFont(new Font(label9.getFont().getName(), Font.BOLD, label9.getFont().getSize()));
        label9.setHorizontalAlignment(4);
        label9.setHorizontalTextPosition(4);
        label9.setMaximumSize(new Dimension(400, 25));
        label9.setMinimumSize(new Dimension(400, 25));
        label9.setPreferredSize(new Dimension(400, 25));
        label9.setText("User ID :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel8.add(label9, gbc);
        txtGetPublicPhotos = new JTextField();
        txtGetPublicPhotos.setMaximumSize(new Dimension(200, 25));
        txtGetPublicPhotos.setMinimumSize(new Dimension(200, 25));
        txtGetPublicPhotos.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel8.add(txtGetPublicPhotos, gbc);
        getPublicPhotosInvoke = new JButton();
        getPublicPhotosInvoke.setActionCommand("Button");
        getPublicPhotosInvoke.setFont(new Font(getPublicPhotosInvoke.getFont().getName(), Font.BOLD,
                                               getPublicPhotosInvoke.getFont().getSize()));
        getPublicPhotosInvoke.setMaximumSize(new Dimension(100, 30));
        getPublicPhotosInvoke.setMinimumSize(new Dimension(100, 30));
        getPublicPhotosInvoke.setOpaque(true);
        getPublicPhotosInvoke.setPreferredSize(new Dimension(100, 30));
        getPublicPhotosInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel8.add(getPublicPhotosInvoke, gbc);
        final JScrollPane scrollPane5 = new JScrollPane();
        scrollPane5.setMaximumSize(new Dimension(550, 225));
        scrollPane5.setMinimumSize(new Dimension(550, 225));
        scrollPane5.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel8.add(scrollPane5, gbc);
        getPublicPhotosOutput = new JTextArea();
        getPublicPhotosOutput.setBackground(new Color(-3355444));
        getPublicPhotosOutput.setEditable(false);
        getPublicPhotosOutput.setText("");
        scrollPane5.setViewportView(getPublicPhotosOutput);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        peopleOperationPane.addTab("GetUploadStatus", panel9);
        final JLabel label10 = new JLabel();
        label10.setFont(
                new Font(label10.getFont().getName(), Font.BOLD, label10.getFont().getSize()));
        label10.setHorizontalAlignment(0);
        label10.setMaximumSize(new Dimension(475, 50));
        label10.setMinimumSize(new Dimension(475, 50));
        label10.setPreferredSize(new Dimension(475, 50));
        label10.setText("Returns information for the calling user related to photo uploads.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel9.add(label10, gbc);
        getUploadStatusInvoke = new JButton();
        getUploadStatusInvoke.setFont(new Font(getUploadStatusInvoke.getFont().getName(), Font.BOLD,
                                               getUploadStatusInvoke.getFont().getSize()));
        getUploadStatusInvoke.setMaximumSize(new Dimension(100, 30));
        getUploadStatusInvoke.setMinimumSize(new Dimension(100, 30));
        getUploadStatusInvoke.setPreferredSize(new Dimension(100, 30));
        getUploadStatusInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel9.add(getUploadStatusInvoke, gbc);
        final JScrollPane scrollPane6 = new JScrollPane();
        scrollPane6.setMaximumSize(new Dimension(550, 225));
        scrollPane6.setMinimumSize(new Dimension(550, 225));
        scrollPane6.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel9.add(scrollPane6, gbc);
        getUploadStatusOutput = new JTextArea();
        getUploadStatusOutput.setBackground(new Color(-3355444));
        getUploadStatusOutput.setEditable(false);
        getUploadStatusOutput.setText("");
        scrollPane6.setViewportView(getUploadStatusOutput);


        populateOperationPane();


        final JPanel panel44 = new JPanel();
        panel44.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Activity", panel44);
        activityOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel44.add(activityOperationPane, gbc);
        final JPanel panel45 = new JPanel();
        panel45.setLayout(new GridBagLayout());
        activityOperationPane.addTab("UserComments", panel45);
        final JLabel label127 = new JLabel();
        label127.setFont(
                new Font(label127.getFont().getName(), Font.BOLD, label127.getFont().getSize()));
        label127.setHorizontalAlignment(0);
        label127.setMaximumSize(new Dimension(800, 50));
        label127.setMinimumSize(new Dimension(800, 50));
        label127.setPreferredSize(new Dimension(800, 50));
        label127.setText(
                "Returns a list of recent activity on photos commented on by the calling user. Do not poll this method more than once an hour.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel45.add(label127, gbc);
        final JLabel label128 = new JLabel();
        label128.setHorizontalAlignment(4);
        label128.setMaximumSize(new Dimension(400, 25));
        label128.setMinimumSize(new Dimension(400, 25));
        label128.setPreferredSize(new Dimension(400, 25));
        label128.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel45.add(label128, gbc);
        txtUserCommentsPage = new JTextField();
        txtUserCommentsPage.setMaximumSize(new Dimension(200, 25));
        txtUserCommentsPage.setMinimumSize(new Dimension(200, 25));
        txtUserCommentsPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel45.add(txtUserCommentsPage, gbc);
        final JLabel label129 = new JLabel();
        label129.setHorizontalAlignment(4);
        label129.setMaximumSize(new Dimension(400, 25));
        label129.setMinimumSize(new Dimension(400, 25));
        label129.setPreferredSize(new Dimension(400, 25));
        label129.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel45.add(label129, gbc);
        cmbUserCommentsPerPage = new JComboBox();
        cmbUserCommentsPerPage.setMaximumSize(new Dimension(200, 25));
        cmbUserCommentsPerPage.setMinimumSize(new Dimension(200, 25));
        cmbUserCommentsPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel45.add(cmbUserCommentsPerPage, gbc);
        userCommentsInvoke = new JButton();
        userCommentsInvoke.setFont(new Font(userCommentsInvoke.getFont().getName(), Font.BOLD,
                                            userCommentsInvoke.getFont().getSize()));
        userCommentsInvoke.setMaximumSize(new Dimension(100, 30));
        userCommentsInvoke.setMinimumSize(new Dimension(100, 30));
        userCommentsInvoke.setPreferredSize(new Dimension(100, 30));
        userCommentsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel45.add(userCommentsInvoke, gbc);
        final JScrollPane scrollPane31 = new JScrollPane();
        scrollPane31.setMaximumSize(new Dimension(550, 225));
        scrollPane31.setMinimumSize(new Dimension(550, 225));
        scrollPane31.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel45.add(scrollPane31, gbc);
        userCommentsOutput = new JTextArea();
        userCommentsOutput.setBackground(new Color(-3355444));
        scrollPane31.setViewportView(userCommentsOutput);
        final JPanel panel46 = new JPanel();
        panel46.setLayout(new GridBagLayout());
        activityOperationPane.addTab("UserPhotos", panel46);
        final JLabel label130 = new JLabel();
        label130.setFont(
                new Font(label130.getFont().getName(), Font.BOLD, label130.getFont().getSize()));
        label130.setHorizontalAlignment(0);
        label130.setMaximumSize(new Dimension(800, 50));
        label130.setMinimumSize(new Dimension(800, 50));
        label130.setPreferredSize(new Dimension(800, 50));
        label130.setText(
                "Returns a list of recent activity on photos belonging to the calling user. Do not poll this method more than once an hour.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel46.add(label130, gbc);
        final JLabel label131 = new JLabel();
        label131.setFont(
                new Font(label131.getFont().getName(), Font.BOLD, label131.getFont().getSize()));
        label131.setHorizontalAlignment(4);
        label131.setMaximumSize(new Dimension(400, 25));
        label131.setMinimumSize(new Dimension(400, 25));
        label131.setPreferredSize(new Dimension(400, 25));
        label131.setText("TimeFrame : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel46.add(label131, gbc);
        txtUserPhotosTimeFrame = new JTextField();
        txtUserPhotosTimeFrame.setMaximumSize(new Dimension(200, 25));
        txtUserPhotosTimeFrame.setMinimumSize(new Dimension(200, 25));
        txtUserPhotosTimeFrame.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel46.add(txtUserPhotosTimeFrame, gbc);
        final JLabel label132 = new JLabel();
        label132.setHorizontalAlignment(4);
        label132.setMaximumSize(new Dimension(400, 25));
        label132.setMinimumSize(new Dimension(400, 25));
        label132.setPreferredSize(new Dimension(400, 25));
        label132.setText("Page          : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel46.add(label132, gbc);
        txtUserPhotosPage = new JTextField();
        txtUserPhotosPage.setMaximumSize(new Dimension(200, 25));
        txtUserPhotosPage.setMinimumSize(new Dimension(200, 25));
        txtUserPhotosPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel46.add(txtUserPhotosPage, gbc);
        final JLabel label133 = new JLabel();
        label133.setHorizontalAlignment(4);
        label133.setMaximumSize(new Dimension(400, 25));
        label133.setMinimumSize(new Dimension(400, 25));
        label133.setPreferredSize(new Dimension(400, 25));
        label133.setText("Per Page    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel46.add(label133, gbc);
        cmbUserPhotosPerPage = new JComboBox();
        cmbUserPhotosPerPage.setMaximumSize(new Dimension(200, 25));
        cmbUserPhotosPerPage.setMinimumSize(new Dimension(200, 25));
        cmbUserPhotosPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel46.add(cmbUserPhotosPerPage, gbc);
        userPhotosInvoke = new JButton();
        userPhotosInvoke.setFont(new Font(userPhotosInvoke.getFont().getName(), Font.BOLD,
                                          userPhotosInvoke.getFont().getSize()));
        userPhotosInvoke.setMaximumSize(new Dimension(100, 30));
        userPhotosInvoke.setMinimumSize(new Dimension(100, 30));
        userPhotosInvoke.setPreferredSize(new Dimension(100, 30));
        userPhotosInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel46.add(userPhotosInvoke, gbc);
        final JScrollPane scrollPane32 = new JScrollPane();
        scrollPane32.setMaximumSize(new Dimension(550, 225));
        scrollPane32.setMinimumSize(new Dimension(550, 225));
        scrollPane32.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel46.add(scrollPane32, gbc);
        userPhotosOutput = new JTextArea();
        userPhotosOutput.setBackground(new Color(-3355444));
        scrollPane32.setViewportView(userPhotosOutput);
        final JPanel panel47 = new JPanel();
        panel47.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Blogs", panel47);
        blogsOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel47.add(blogsOperationPane, gbc);
        final JPanel panel48 = new JPanel();
        panel48.setLayout(new GridBagLayout());
        blogsOperationPane.addTab("GetList", panel48);
        final JLabel label134 = new JLabel();
        label134.setFont(
                new Font(label134.getFont().getName(), Font.BOLD, label134.getFont().getSize()));
        label134.setHorizontalAlignment(0);
        label134.setHorizontalTextPosition(0);
        label134.setMaximumSize(new Dimension(400, 50));
        label134.setMinimumSize(new Dimension(400, 50));
        label134.setPreferredSize(new Dimension(400, 50));
        label134.setText("Get a list of configured blogs for the calling user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel48.add(label134, gbc);
        final JScrollPane scrollPane33 = new JScrollPane();
        scrollPane33.setBackground(new Color(-3355444));
        scrollPane33.setMaximumSize(new Dimension(550, 225));
        scrollPane33.setMinimumSize(new Dimension(550, 225));
        scrollPane33.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel48.add(scrollPane33, gbc);
        blogsGetListOutput = new JTextArea();
        blogsGetListOutput.setBackground(new Color(-3355444));
        blogsGetListOutput.setEditable(false);
        blogsGetListOutput.setText("");
        scrollPane33.setViewportView(blogsGetListOutput);
        blogsGetListInvoke = new JButton();
        blogsGetListInvoke.setFocusCycleRoot(true);
        blogsGetListInvoke.setFont(
                new Font(blogsGetListInvoke.getFont().getName(), Font.BOLD,
                         blogsGetListInvoke.getFont().getSize()));
        blogsGetListInvoke.setMaximumSize(new Dimension(100, 30));
        blogsGetListInvoke.setMinimumSize(new Dimension(100, 30));
        blogsGetListInvoke.setPreferredSize(new Dimension(100, 30));
        blogsGetListInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel48.add(blogsGetListInvoke, gbc);
        final JPanel panel49 = new JPanel();
        panel49.setLayout(new GridBagLayout());
        blogsOperationPane.addTab("PostPhoto", panel49);
        final JLabel label135 = new JLabel();
        label135.setFont(
                new Font(label135.getFont().getName(), Font.BOLD, label135.getFont().getSize()));
        label135.setHorizontalAlignment(0);
        label135.setMaximumSize(new Dimension(600, 50));
        label135.setMinimumSize(new Dimension(600, 50));
        label135.setPreferredSize(new Dimension(600, 50));
        label135.setText("Post a photo to a Blog.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel49.add(label135, gbc);
        final JLabel label136 = new JLabel();
        label136.setFont(
                new Font(label136.getFont().getName(), Font.BOLD, label136.getFont().getSize()));
        label136.setHorizontalAlignment(4);
        label136.setMaximumSize(new Dimension(400, 25));
        label136.setMinimumSize(new Dimension(400, 25));
        label136.setPreferredSize(new Dimension(400, 25));
        label136.setText("Blog ID          : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel49.add(label136, gbc);
        txtPostPhotoBlogID = new JTextField();
        txtPostPhotoBlogID.setMaximumSize(new Dimension(200, 25));
        txtPostPhotoBlogID.setMinimumSize(new Dimension(200, 25));
        txtPostPhotoBlogID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel49.add(txtPostPhotoBlogID, gbc);
        final JLabel label137 = new JLabel();
        label137.setFont(
                new Font(label137.getFont().getName(), Font.BOLD, label137.getFont().getSize()));
        label137.setHorizontalAlignment(4);
        label137.setMaximumSize(new Dimension(400, 25));
        label137.setMinimumSize(new Dimension(400, 25));
        label137.setPreferredSize(new Dimension(400, 25));
        label137.setText("Photo ID        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel49.add(label137, gbc);
        txtPostPhotoPhotoID = new JTextField();
        txtPostPhotoPhotoID.setMaximumSize(new Dimension(200, 25));
        txtPostPhotoPhotoID.setMinimumSize(new Dimension(200, 25));
        txtPostPhotoPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel49.add(txtPostPhotoPhotoID, gbc);
        postPhotoInvoke = new JButton();
        postPhotoInvoke.setFont(new Font(postPhotoInvoke.getFont().getName(), Font.BOLD,
                                         postPhotoInvoke.getFont().getSize()));
        postPhotoInvoke.setMaximumSize(new Dimension(100, 30));
        postPhotoInvoke.setMinimumSize(new Dimension(100, 30));
        postPhotoInvoke.setPreferredSize(new Dimension(100, 30));
        postPhotoInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel49.add(postPhotoInvoke, gbc);
        final JScrollPane scrollPane34 = new JScrollPane();
        scrollPane34.setMaximumSize(new Dimension(550, 225));
        scrollPane34.setMinimumSize(new Dimension(550, 225));
        scrollPane34.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel49.add(scrollPane34, gbc);
        postPhotoOutput = new JTextArea();
        postPhotoOutput.setBackground(new Color(-3355444));
        scrollPane34.setViewportView(postPhotoOutput);
        txtPostPhotoTitle = new JTextField();
        txtPostPhotoTitle.setMaximumSize(new Dimension(200, 25));
        txtPostPhotoTitle.setMinimumSize(new Dimension(200, 25));
        txtPostPhotoTitle.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel49.add(txtPostPhotoTitle, gbc);
        final JLabel label138 = new JLabel();
        label138.setFont(
                new Font(label138.getFont().getName(), Font.BOLD, label138.getFont().getSize()));
        label138.setHorizontalAlignment(4);
        label138.setMaximumSize(new Dimension(400, 25));
        label138.setMinimumSize(new Dimension(400, 25));
        label138.setPreferredSize(new Dimension(400, 25));
        label138.setText("Title               : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel49.add(label138, gbc);
        final JLabel label139 = new JLabel();
        label139.setHorizontalAlignment(4);
        label139.setMaximumSize(new Dimension(400, 25));
        label139.setMinimumSize(new Dimension(400, 25));
        label139.setPreferredSize(new Dimension(400, 25));
        label139.setText("Blog Password : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel49.add(label139, gbc);
        txtPostPhotoPassword = new JTextField();
        txtPostPhotoPassword.setMaximumSize(new Dimension(200, 25));
        txtPostPhotoPassword.setMinimumSize(new Dimension(200, 25));
        txtPostPhotoPassword.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel49.add(txtPostPhotoPassword, gbc);
        txtPostPhotoDescription = new JTextField();
        txtPostPhotoDescription.setMaximumSize(new Dimension(200, 25));
        txtPostPhotoDescription.setMinimumSize(new Dimension(200, 25));
        txtPostPhotoDescription.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel49.add(txtPostPhotoDescription, gbc);
        final JLabel label140 = new JLabel();
        label140.setFont(
                new Font(label140.getFont().getName(), Font.BOLD, label140.getFont().getSize()));
        label140.setHorizontalAlignment(4);
        label140.setMaximumSize(new Dimension(400, 25));
        label140.setMinimumSize(new Dimension(400, 25));
        label140.setPreferredSize(new Dimension(400, 25));
        label140.setText("Description    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel49.add(label140, gbc);
        final JPanel panel50 = new JPanel();
        panel50.setLayout(new GridBagLayout());
        tabbedPane1.addTab("PhotoSetsComments", panel50);
        photoSetsCommentsOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel50.add(photoSetsCommentsOperationPane, gbc);
        final JPanel panel51 = new JPanel();
        panel51.setLayout(new GridBagLayout());
        photoSetsCommentsOperationPane.addTab("AddComment", panel51);
        final JLabel label141 = new JLabel();
        label141.setFont(
                new Font(label141.getFont().getName(), Font.BOLD, label141.getFont().getSize()));
        label141.setHorizontalAlignment(4);
        label141.setMaximumSize(new Dimension(400, 25));
        label141.setMinimumSize(new Dimension(400, 25));
        label141.setPreferredSize(new Dimension(400, 25));
        label141.setText("PhotoSet ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel51.add(label141, gbc);
        txtPhotoSetsCommAddID = new JTextField();
        txtPhotoSetsCommAddID.setMaximumSize(new Dimension(200, 25));
        txtPhotoSetsCommAddID.setMinimumSize(new Dimension(200, 25));
        txtPhotoSetsCommAddID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel51.add(txtPhotoSetsCommAddID, gbc);
        final JLabel label142 = new JLabel();
        label142.setHorizontalAlignment(4);
        label142.setMaximumSize(new Dimension(400, 25));
        label142.setMinimumSize(new Dimension(400, 25));
        label142.setPreferredSize(new Dimension(400, 25));
        label142.setText("Comment      :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel51.add(label142, gbc);
        PhotoSetsCommAddComment = new JTextField();
        PhotoSetsCommAddComment.setMaximumSize(new Dimension(200, 25));
        PhotoSetsCommAddComment.setMinimumSize(new Dimension(200, 25));
        PhotoSetsCommAddComment.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel51.add(PhotoSetsCommAddComment, gbc);
        photoSetsCommAddInvoke = new JButton();
        photoSetsCommAddInvoke.setFont(new Font(photoSetsCommAddInvoke.getFont().getName(),
                                                Font.BOLD,
                                                photoSetsCommAddInvoke.getFont().getSize()));
        photoSetsCommAddInvoke.setLabel("Invoke");
        photoSetsCommAddInvoke.setMaximumSize(new Dimension(100, 30));
        photoSetsCommAddInvoke.setMinimumSize(new Dimension(100, 30));
        photoSetsCommAddInvoke.setPreferredSize(new Dimension(100, 30));
        photoSetsCommAddInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel51.add(photoSetsCommAddInvoke, gbc);
        final JLabel label143 = new JLabel();
        label143.setFont(
                new Font(label143.getFont().getName(), Font.BOLD, label143.getFont().getSize()));
        label143.setHorizontalAlignment(0);
        label143.setMaximumSize(new Dimension(600, 50));
        label143.setMinimumSize(new Dimension(600, 50));
        label143.setPreferredSize(new Dimension(600, 50));
        label143.setText("Add a comment to a photoset.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel51.add(label143, gbc);
        final JScrollPane scrollPane35 = new JScrollPane();
        scrollPane35.setMaximumSize(new Dimension(550, 225));
        scrollPane35.setMinimumSize(new Dimension(550, 225));
        scrollPane35.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel51.add(scrollPane35, gbc);
        photoSetsCommAddOutput = new JTextArea();
        photoSetsCommAddOutput.setBackground(new Color(-3355444));
        scrollPane35.setViewportView(photoSetsCommAddOutput);
        final JPanel panel52 = new JPanel();
        panel52.setLayout(new GridBagLayout());
        photoSetsCommentsOperationPane.addTab("DeleteComment", panel52);
        final JLabel label144 = new JLabel();
        label144.setFont(
                new Font(label144.getFont().getName(), Font.BOLD, label144.getFont().getSize()));
        label144.setHorizontalAlignment(0);
        label144.setMaximumSize(new Dimension(600, 50));
        label144.setMinimumSize(new Dimension(600, 50));
        label144.setPreferredSize(new Dimension(600, 50));
        label144.setText("Delete a photoset comment as the currently authenticated user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel52.add(label144, gbc);
        final JLabel label145 = new JLabel();
        label145.setFont(
                new Font(label145.getFont().getName(), Font.BOLD, label145.getFont().getSize()));
        label145.setHorizontalAlignment(4);
        label145.setMaximumSize(new Dimension(400, 25));
        label145.setMinimumSize(new Dimension(400, 25));
        label145.setPreferredSize(new Dimension(400, 25));
        label145.setText("Comment ID : ");
        label145.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel52.add(label145, gbc);
        txtPhotoSetsCommDelID = new JTextField();
        txtPhotoSetsCommDelID.setMaximumSize(new Dimension(200, 25));
        txtPhotoSetsCommDelID.setMinimumSize(new Dimension(200, 25));
        txtPhotoSetsCommDelID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel52.add(txtPhotoSetsCommDelID, gbc);
        photoSetsCommDelInvoke = new JButton();
        photoSetsCommDelInvoke.setFont(new Font(photoSetsCommDelInvoke.getFont().getName(),
                                                Font.BOLD,
                                                photoSetsCommDelInvoke.getFont().getSize()));
        photoSetsCommDelInvoke.setMaximumSize(new Dimension(100, 30));
        photoSetsCommDelInvoke.setMinimumSize(new Dimension(100, 30));
        photoSetsCommDelInvoke.setPreferredSize(new Dimension(100, 30));
        photoSetsCommDelInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel52.add(photoSetsCommDelInvoke, gbc);
        final JScrollPane scrollPane36 = new JScrollPane();
        scrollPane36.setMaximumSize(new Dimension(550, 225));
        scrollPane36.setMinimumSize(new Dimension(550, 225));
        scrollPane36.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel52.add(scrollPane36, gbc);
        photoSetsCommDelOutput = new JTextArea();
        photoSetsCommDelOutput.setBackground(new Color(-3355444));
        photoSetsCommDelOutput.setEditable(false);
        scrollPane36.setViewportView(photoSetsCommDelOutput);
        final JPanel panel53 = new JPanel();
        panel53.setLayout(new GridBagLayout());
        photoSetsCommentsOperationPane.addTab("EditComment", panel53);
        final JLabel label146 = new JLabel();
        label146.setFont(
                new Font(label146.getFont().getName(), Font.BOLD, label146.getFont().getSize()));
        label146.setHorizontalAlignment(4);
        label146.setMaximumSize(new Dimension(400, 25));
        label146.setMinimumSize(new Dimension(400, 25));
        label146.setPreferredSize(new Dimension(400, 25));
        label146.setText("Comment ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel53.add(label146, gbc);
        txtPhotoSetsCommEditID = new JTextField();
        txtPhotoSetsCommEditID.setMaximumSize(new Dimension(200, 25));
        txtPhotoSetsCommEditID.setMinimumSize(new Dimension(200, 25));
        txtPhotoSetsCommEditID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel53.add(txtPhotoSetsCommEditID, gbc);
        final JLabel label147 = new JLabel();
        label147.setHorizontalAlignment(4);
        label147.setMaximumSize(new Dimension(400, 25));
        label147.setMinimumSize(new Dimension(400, 25));
        label147.setPreferredSize(new Dimension(400, 25));
        label147.setText("Comment        :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel53.add(label147, gbc);
        txtPhotoSetsCommEditComment = new JTextField();
        txtPhotoSetsCommEditComment.setMaximumSize(new Dimension(200, 25));
        txtPhotoSetsCommEditComment.setMinimumSize(new Dimension(200, 25));
        txtPhotoSetsCommEditComment.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel53.add(txtPhotoSetsCommEditComment, gbc);
        photoSetsCommEditInvoke = new JButton();
        photoSetsCommEditInvoke.setFont(new Font(photoSetsCommEditInvoke.getFont().getName(),
                                                 Font.BOLD,
                                                 photoSetsCommEditInvoke.getFont().getSize()));
        photoSetsCommEditInvoke.setLabel("Invoke");
        photoSetsCommEditInvoke.setMaximumSize(new Dimension(100, 30));
        photoSetsCommEditInvoke.setMinimumSize(new Dimension(100, 30));
        photoSetsCommEditInvoke.setPreferredSize(new Dimension(100, 30));
        photoSetsCommEditInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel53.add(photoSetsCommEditInvoke, gbc);
        final JLabel label148 = new JLabel();
        label148.setFont(
                new Font(label148.getFont().getName(), Font.BOLD, label148.getFont().getSize()));
        label148.setHorizontalAlignment(0);
        label148.setMaximumSize(new Dimension(600, 50));
        label148.setMinimumSize(new Dimension(600, 50));
        label148.setPreferredSize(new Dimension(600, 50));
        label148.setText("Edit the text of a comment as the currently authenticated user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel53.add(label148, gbc);
        final JScrollPane scrollPane37 = new JScrollPane();
        scrollPane37.setMaximumSize(new Dimension(550, 225));
        scrollPane37.setMinimumSize(new Dimension(550, 225));
        scrollPane37.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel53.add(scrollPane37, gbc);
        PhotoSetsCommEditOutput = new JTextArea();
        PhotoSetsCommEditOutput.setBackground(new Color(-3355444));
        scrollPane37.setViewportView(PhotoSetsCommEditOutput);
        final JPanel panel54 = new JPanel();
        panel54.setLayout(new GridBagLayout());
        photoSetsCommentsOperationPane.addTab("GetList", panel54);
        final JLabel label149 = new JLabel();
        label149.setFont(
                new Font(label149.getFont().getName(), Font.BOLD, label149.getFont().getSize()));
        label149.setHorizontalAlignment(0);
        label149.setMaximumSize(new Dimension(600, 50));
        label149.setMinimumSize(new Dimension(600, 50));
        label149.setPreferredSize(new Dimension(600, 50));
        label149.setText("Returns the comments for a photoset.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel54.add(label149, gbc);
        final JLabel label150 = new JLabel();
        label150.setFont(
                new Font(label150.getFont().getName(), Font.BOLD, label150.getFont().getSize()));
        label150.setHorizontalAlignment(4);
        label150.setMaximumSize(new Dimension(400, 25));
        label150.setMinimumSize(new Dimension(400, 25));
        label150.setPreferredSize(new Dimension(400, 25));
        label150.setText("PhotoSet ID : ");
        label150.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel54.add(label150, gbc);
        txtPhotoSetsCommGetID = new JTextField();
        txtPhotoSetsCommGetID.setMaximumSize(new Dimension(200, 25));
        txtPhotoSetsCommGetID.setMinimumSize(new Dimension(200, 25));
        txtPhotoSetsCommGetID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel54.add(txtPhotoSetsCommGetID, gbc);
        photoSetsCommGetInvoke = new JButton();
        photoSetsCommGetInvoke.setFont(new Font(photoSetsCommGetInvoke.getFont().getName(),
                                                Font.BOLD,
                                                photoSetsCommGetInvoke.getFont().getSize()));
        photoSetsCommGetInvoke.setMaximumSize(new Dimension(100, 30));
        photoSetsCommGetInvoke.setMinimumSize(new Dimension(100, 30));
        photoSetsCommGetInvoke.setPreferredSize(new Dimension(100, 30));
        photoSetsCommGetInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel54.add(photoSetsCommGetInvoke, gbc);
        final JScrollPane scrollPane38 = new JScrollPane();
        scrollPane38.setMaximumSize(new Dimension(550, 225));
        scrollPane38.setMinimumSize(new Dimension(550, 225));
        scrollPane38.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel54.add(scrollPane38, gbc);
        photoSetsCommGetOutput = new JTextArea();
        photoSetsCommGetOutput.setBackground(new Color(-3355444));
        photoSetsCommGetOutput.setEditable(false);
        scrollPane38.setViewportView(photoSetsCommGetOutput);
        final JPanel panel55 = new JPanel();
        panel55.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Contacts", panel55);
        contactsOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel55.add(contactsOperationPane, gbc);
        final JPanel panel56 = new JPanel();
        panel56.setLayout(new GridBagLayout());
        contactsOperationPane.addTab("GetList", panel56);
        final JLabel label151 = new JLabel();
        label151.setFont(
                new Font(label151.getFont().getName(), Font.BOLD, label151.getFont().getSize()));
        label151.setHorizontalAlignment(0);
        label151.setMaximumSize(new Dimension(600, 50));
        label151.setMinimumSize(new Dimension(600, 50));
        label151.setPreferredSize(new Dimension(600, 50));
        label151.setText("Get a list of contacts for the calling user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel56.add(label151, gbc);
        final JLabel label152 = new JLabel();
        label152.setFont(new Font(label152.getFont().getName(), label152.getFont().getStyle(),
                                  label152.getFont().getSize()));
        label152.setHorizontalAlignment(4);
        label152.setMaximumSize(new Dimension(400, 25));
        label152.setMinimumSize(new Dimension(400, 25));
        label152.setPreferredSize(new Dimension(400, 25));
        label152.setText("Filter       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel56.add(label152, gbc);
        final JLabel label153 = new JLabel();
        label153.setHorizontalAlignment(4);
        label153.setMaximumSize(new Dimension(400, 25));
        label153.setMinimumSize(new Dimension(400, 25));
        label153.setPreferredSize(new Dimension(400, 25));
        label153.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel56.add(label153, gbc);
        txtContactsGetPage = new JTextField();
        txtContactsGetPage.setMaximumSize(new Dimension(200, 25));
        txtContactsGetPage.setMinimumSize(new Dimension(200, 25));
        txtContactsGetPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel56.add(txtContactsGetPage, gbc);
        final JLabel label154 = new JLabel();
        label154.setHorizontalAlignment(4);
        label154.setMaximumSize(new Dimension(400, 25));
        label154.setMinimumSize(new Dimension(400, 25));
        label154.setPreferredSize(new Dimension(400, 25));
        label154.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel56.add(label154, gbc);
        cmbContactsGetPerPage = new JComboBox();
        cmbContactsGetPerPage.setMaximumSize(new Dimension(200, 25));
        cmbContactsGetPerPage.setMinimumSize(new Dimension(200, 25));
        cmbContactsGetPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel56.add(cmbContactsGetPerPage, gbc);
        contactsGetInvoke = new JButton();
        contactsGetInvoke.setFont(new Font(contactsGetInvoke.getFont().getName(), Font.BOLD,
                                           contactsGetInvoke.getFont().getSize()));
        contactsGetInvoke.setMaximumSize(new Dimension(100, 30));
        contactsGetInvoke.setMinimumSize(new Dimension(100, 30));
        contactsGetInvoke.setPreferredSize(new Dimension(100, 30));
        contactsGetInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel56.add(contactsGetInvoke, gbc);
        final JScrollPane scrollPane39 = new JScrollPane();
        scrollPane39.setMaximumSize(new Dimension(550, 225));
        scrollPane39.setMinimumSize(new Dimension(550, 225));
        scrollPane39.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel56.add(scrollPane39, gbc);
        contactsGetOutput = new JTextArea();
        contactsGetOutput.setBackground(new Color(-3355444));
        scrollPane39.setViewportView(contactsGetOutput);
        cmbContactsGetFilter = new JComboBox();
        cmbContactsGetFilter.setMaximumSize(new Dimension(200, 25));
        cmbContactsGetFilter.setMinimumSize(new Dimension(200, 25));
        cmbContactsGetFilter.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel56.add(cmbContactsGetFilter, gbc);
        final JPanel panel57 = new JPanel();
        panel57.setLayout(new GridBagLayout());
        contactsOperationPane.addTab("GetPublicList ", panel57);
        final JLabel label155 = new JLabel();
        label155.setFont(
                new Font(label155.getFont().getName(), Font.BOLD, label155.getFont().getSize()));
        label155.setHorizontalAlignment(0);
        label155.setMaximumSize(new Dimension(600, 50));
        label155.setMinimumSize(new Dimension(600, 50));
        label155.setPreferredSize(new Dimension(600, 50));
        label155.setText("Get the contact list for a user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel57.add(label155, gbc);
        final JLabel label156 = new JLabel();
        label156.setFont(
                new Font(label156.getFont().getName(), Font.BOLD, label156.getFont().getSize()));
        label156.setHorizontalAlignment(4);
        label156.setMaximumSize(new Dimension(400, 25));
        label156.setMinimumSize(new Dimension(400, 25));
        label156.setPreferredSize(new Dimension(400, 25));
        label156.setText("User ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel57.add(label156, gbc);
        final JLabel label157 = new JLabel();
        label157.setHorizontalAlignment(4);
        label157.setMaximumSize(new Dimension(400, 25));
        label157.setMinimumSize(new Dimension(400, 25));
        label157.setPreferredSize(new Dimension(400, 25));
        label157.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel57.add(label157, gbc);
        txtContactsGetPubPage = new JTextField();
        txtContactsGetPubPage.setMaximumSize(new Dimension(200, 25));
        txtContactsGetPubPage.setMinimumSize(new Dimension(200, 25));
        txtContactsGetPubPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel57.add(txtContactsGetPubPage, gbc);
        final JLabel label158 = new JLabel();
        label158.setHorizontalAlignment(4);
        label158.setMaximumSize(new Dimension(400, 25));
        label158.setMinimumSize(new Dimension(400, 25));
        label158.setPreferredSize(new Dimension(400, 25));
        label158.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel57.add(label158, gbc);
        cmbContactsGetPubPerPage = new JComboBox();
        cmbContactsGetPubPerPage.setMaximumSize(new Dimension(200, 25));
        cmbContactsGetPubPerPage.setMinimumSize(new Dimension(200, 25));
        cmbContactsGetPubPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel57.add(cmbContactsGetPubPerPage, gbc);
        contactsGetPubInvoke = new JButton();
        contactsGetPubInvoke.setFont(new Font(contactsGetPubInvoke.getFont().getName(), Font.BOLD,
                                              contactsGetPubInvoke.getFont().getSize()));
        contactsGetPubInvoke.setMaximumSize(new Dimension(100, 30));
        contactsGetPubInvoke.setMinimumSize(new Dimension(100, 30));
        contactsGetPubInvoke.setPreferredSize(new Dimension(100, 30));
        contactsGetPubInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel57.add(contactsGetPubInvoke, gbc);
        final JScrollPane scrollPane40 = new JScrollPane();
        scrollPane40.setMaximumSize(new Dimension(550, 225));
        scrollPane40.setMinimumSize(new Dimension(550, 225));
        scrollPane40.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel57.add(scrollPane40, gbc);
        contactsGetPubOutput = new JTextArea();
        contactsGetPubOutput.setBackground(new Color(-3355444));
        scrollPane40.setViewportView(contactsGetPubOutput);
        txtContactsGetPubID = new JTextField();
        txtContactsGetPubID.setMaximumSize(new Dimension(200, 25));
        txtContactsGetPubID.setMinimumSize(new Dimension(200, 25));
        txtContactsGetPubID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel57.add(txtContactsGetPubID, gbc);
        final JPanel panel58 = new JPanel();
        panel58.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Favorites", panel58);
        favoritesOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel58.add(favoritesOperationPane, gbc);
        final JPanel panel59 = new JPanel();
        panel59.setLayout(new GridBagLayout());
        favoritesOperationPane.addTab("Add", panel59);
        final JLabel label159 = new JLabel();
        label159.setFont(
                new Font(label159.getFont().getName(), Font.BOLD, label159.getFont().getSize()));
        label159.setHorizontalAlignment(0);
        label159.setMaximumSize(new Dimension(600, 50));
        label159.setMinimumSize(new Dimension(600, 50));
        label159.setPreferredSize(new Dimension(600, 50));
        label159.setText("Adds a photo to a user's favorites list.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel59.add(label159, gbc);
        final JLabel label160 = new JLabel();
        label160.setFont(
                new Font(label160.getFont().getName(), Font.BOLD, label160.getFont().getSize()));
        label160.setHorizontalAlignment(4);
        label160.setMaximumSize(new Dimension(400, 25));
        label160.setMinimumSize(new Dimension(400, 25));
        label160.setPreferredSize(new Dimension(400, 25));
        label160.setText("Photo ID : ");
        label160.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel59.add(label160, gbc);
        txtFavoritesAddID = new JTextField();
        txtFavoritesAddID.setMaximumSize(new Dimension(200, 25));
        txtFavoritesAddID.setMinimumSize(new Dimension(200, 25));
        txtFavoritesAddID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel59.add(txtFavoritesAddID, gbc);
        favoritesAddInvoke = new JButton();
        favoritesAddInvoke.setFont(new Font(favoritesAddInvoke.getFont().getName(), Font.BOLD,
                                            favoritesAddInvoke.getFont().getSize()));
        favoritesAddInvoke.setMaximumSize(new Dimension(100, 30));
        favoritesAddInvoke.setMinimumSize(new Dimension(100, 30));
        favoritesAddInvoke.setPreferredSize(new Dimension(100, 30));
        favoritesAddInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel59.add(favoritesAddInvoke, gbc);
        final JScrollPane scrollPane41 = new JScrollPane();
        scrollPane41.setMaximumSize(new Dimension(550, 225));
        scrollPane41.setMinimumSize(new Dimension(550, 225));
        scrollPane41.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel59.add(scrollPane41, gbc);
        favoritesAddOutput = new JTextArea();
        favoritesAddOutput.setBackground(new Color(-3355444));
        favoritesAddOutput.setEditable(false);
        scrollPane41.setViewportView(favoritesAddOutput);
        final JPanel panel60 = new JPanel();
        panel60.setLayout(new GridBagLayout());
        favoritesOperationPane.addTab("GetList", panel60);
        final JLabel label161 = new JLabel();
        label161.setFont(
                new Font(label161.getFont().getName(), Font.BOLD, label161.getFont().getSize()));
        label161.setHorizontalAlignment(0);
        label161.setMaximumSize(new Dimension(800, 50));
        label161.setMinimumSize(new Dimension(800, 50));
        label161.setPreferredSize(new Dimension(800, 50));
        label161.setText(
                "Returns a list of the user's favorite photos. Only photos which the calling user has permission to see are returned.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel60.add(label161, gbc);
        txtFavoritesGetPage = new JTextField();
        txtFavoritesGetPage.setMaximumSize(new Dimension(200, 25));
        txtFavoritesGetPage.setMinimumSize(new Dimension(200, 25));
        txtFavoritesGetPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel60.add(txtFavoritesGetPage, gbc);
        final JLabel label162 = new JLabel();
        label162.setHorizontalAlignment(4);
        label162.setMaximumSize(new Dimension(400, 25));
        label162.setMinimumSize(new Dimension(400, 25));
        label162.setPreferredSize(new Dimension(400, 25));
        label162.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel60.add(label162, gbc);
        cmbFavoritesGetPerPage = new JComboBox();
        cmbFavoritesGetPerPage.setMaximumSize(new Dimension(200, 25));
        cmbFavoritesGetPerPage.setMinimumSize(new Dimension(200, 25));
        cmbFavoritesGetPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel60.add(cmbFavoritesGetPerPage, gbc);
        favoritesGetInvoke = new JButton();
        favoritesGetInvoke.setFont(new Font(favoritesGetInvoke.getFont().getName(), Font.BOLD,
                                            favoritesGetInvoke.getFont().getSize()));
        favoritesGetInvoke.setMaximumSize(new Dimension(100, 30));
        favoritesGetInvoke.setMinimumSize(new Dimension(100, 30));
        favoritesGetInvoke.setPreferredSize(new Dimension(100, 30));
        favoritesGetInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 0, 0);
        panel60.add(favoritesGetInvoke, gbc);
        final JPanel panel61 = new JPanel();
        panel61.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel60.add(panel61, gbc);
        final JLabel label163 = new JLabel();
        label163.setFont(
                new Font(label163.getFont().getName(), Font.BOLD, label163.getFont().getSize()));
        label163.setMaximumSize(new Dimension(400, 25));
        label163.setMinimumSize(new Dimension(400, 25));
        label163.setPreferredSize(new Dimension(400, 25));
        label163.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(label163, gbc);
        chkFavoritesGetLicense = new JCheckBox();
        chkFavoritesGetLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetLicense, gbc);
        chkFavoritesGetDateUp = new JCheckBox();
        chkFavoritesGetDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetDateUp, gbc);
        chkFavoritesGetDateTak = new JCheckBox();
        chkFavoritesGetDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetDateTak, gbc);
        chkFavoritesGetOwner = new JCheckBox();
        chkFavoritesGetOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetOwner, gbc);
        chkFavoritesGetServer = new JCheckBox();
        chkFavoritesGetServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetServer, gbc);
        chkFavoritesGetOriginal = new JCheckBox();
        chkFavoritesGetOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetOriginal, gbc);
        chkFavoritesGetLastUp = new JCheckBox();
        chkFavoritesGetLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetLastUp, gbc);
        chkFavoritesGetGeo = new JCheckBox();
        chkFavoritesGetGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetGeo, gbc);
        chkFavoritesGetTags = new JCheckBox();
        chkFavoritesGetTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetTags, gbc);
        chkFavoritesGetMachine = new JCheckBox();
        chkFavoritesGetMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel61.add(chkFavoritesGetMachine, gbc);
        final JScrollPane scrollPane42 = new JScrollPane();
        scrollPane42.setMaximumSize(new Dimension(550, 225));
        scrollPane42.setMinimumSize(new Dimension(550, 225));
        scrollPane42.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel60.add(scrollPane42, gbc);
        favoritesGetOutput = new JTextArea();
        favoritesGetOutput.setBackground(new Color(-3355444));
        scrollPane42.setViewportView(favoritesGetOutput);
        final JLabel label164 = new JLabel();
        label164.setHorizontalAlignment(4);
        label164.setMaximumSize(new Dimension(400, 25));
        label164.setMinimumSize(new Dimension(400, 25));
        label164.setPreferredSize(new Dimension(400, 25));
        label164.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel60.add(label164, gbc);
        txtFavoritesGetID = new JTextField();
        txtFavoritesGetID.setMaximumSize(new Dimension(200, 25));
        txtFavoritesGetID.setMinimumSize(new Dimension(200, 25));
        txtFavoritesGetID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel60.add(txtFavoritesGetID, gbc);
        final JLabel label165 = new JLabel();
        label165.setHorizontalAlignment(4);
        label165.setMaximumSize(new Dimension(400, 25));
        label165.setMinimumSize(new Dimension(400, 25));
        label165.setPreferredSize(new Dimension(400, 25));
        label165.setText("User ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel60.add(label165, gbc);
        final JPanel panel62 = new JPanel();
        panel62.setLayout(new GridBagLayout());
        favoritesOperationPane.addTab("GetPublicList", panel62);
        final JLabel label166 = new JLabel();
        label166.setFont(
                new Font(label166.getFont().getName(), Font.BOLD, label166.getFont().getSize()));
        label166.setHorizontalAlignment(0);
        label166.setMaximumSize(new Dimension(800, 50));
        label166.setMinimumSize(new Dimension(800, 50));
        label166.setPreferredSize(new Dimension(800, 50));
        label166.setText("Returns a list of favorite public photos for the given user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel62.add(label166, gbc);
        txtFavoritesGetPubPage = new JTextField();
        txtFavoritesGetPubPage.setMaximumSize(new Dimension(200, 25));
        txtFavoritesGetPubPage.setMinimumSize(new Dimension(200, 25));
        txtFavoritesGetPubPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel62.add(txtFavoritesGetPubPage, gbc);
        final JLabel label167 = new JLabel();
        label167.setHorizontalAlignment(4);
        label167.setMaximumSize(new Dimension(400, 25));
        label167.setMinimumSize(new Dimension(400, 25));
        label167.setPreferredSize(new Dimension(400, 25));
        label167.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel62.add(label167, gbc);
        cmbFavoritesGetPubPerPage = new JComboBox();
        cmbFavoritesGetPubPerPage.setMaximumSize(new Dimension(200, 25));
        cmbFavoritesGetPubPerPage.setMinimumSize(new Dimension(200, 25));
        cmbFavoritesGetPubPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel62.add(cmbFavoritesGetPubPerPage, gbc);
        favoritesGetPubInvoke = new JButton();
        favoritesGetPubInvoke.setFont(new Font(favoritesGetPubInvoke.getFont().getName(), Font.BOLD,
                                               favoritesGetPubInvoke.getFont().getSize()));
        favoritesGetPubInvoke.setMaximumSize(new Dimension(100, 30));
        favoritesGetPubInvoke.setMinimumSize(new Dimension(100, 30));
        favoritesGetPubInvoke.setPreferredSize(new Dimension(100, 30));
        favoritesGetPubInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 0, 0);
        panel62.add(favoritesGetPubInvoke, gbc);
        final JPanel panel63 = new JPanel();
        panel63.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel62.add(panel63, gbc);
        final JLabel label168 = new JLabel();
        label168.setFont(
                new Font(label168.getFont().getName(), Font.BOLD, label168.getFont().getSize()));
        label168.setMaximumSize(new Dimension(400, 25));
        label168.setMinimumSize(new Dimension(400, 25));
        label168.setPreferredSize(new Dimension(400, 25));
        label168.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(label168, gbc);
        chkFavoritesGetPubLicense = new JCheckBox();
        chkFavoritesGetPubLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubLicense, gbc);
        chkFavoritesGetPubDateUp = new JCheckBox();
        chkFavoritesGetPubDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubDateUp, gbc);
        chkFavoritesGetPubDateTak = new JCheckBox();
        chkFavoritesGetPubDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubDateTak, gbc);
        chkFavoritesGetPubOwner = new JCheckBox();
        chkFavoritesGetPubOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubOwner, gbc);
        chkFavoritesGetPubServer = new JCheckBox();
        chkFavoritesGetPubServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubServer, gbc);
        chkFavoritesGetPubOriginal = new JCheckBox();
        chkFavoritesGetPubOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubOriginal, gbc);
        chkFavoritesGetPubLastUp = new JCheckBox();
        chkFavoritesGetPubLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubLastUp, gbc);
        chkFavoritesGetPubGeo = new JCheckBox();
        chkFavoritesGetPubGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubGeo, gbc);
        chkFavoritesGetPubTags = new JCheckBox();
        chkFavoritesGetPubTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubTags, gbc);
        chkFavoritesGetPubMachine = new JCheckBox();
        chkFavoritesGetPubMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel63.add(chkFavoritesGetPubMachine, gbc);
        final JScrollPane scrollPane43 = new JScrollPane();
        scrollPane43.setMaximumSize(new Dimension(550, 225));
        scrollPane43.setMinimumSize(new Dimension(550, 225));
        scrollPane43.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel62.add(scrollPane43, gbc);
        favoritesGetPubOutput = new JTextArea();
        favoritesGetPubOutput.setBackground(new Color(-3355444));
        scrollPane43.setViewportView(favoritesGetPubOutput);
        final JLabel label169 = new JLabel();
        label169.setHorizontalAlignment(4);
        label169.setMaximumSize(new Dimension(400, 25));
        label169.setMinimumSize(new Dimension(400, 25));
        label169.setPreferredSize(new Dimension(400, 25));
        label169.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel62.add(label169, gbc);
        txtFavoritesGetPubID = new JTextField();
        txtFavoritesGetPubID.setMaximumSize(new Dimension(200, 25));
        txtFavoritesGetPubID.setMinimumSize(new Dimension(200, 25));
        txtFavoritesGetPubID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel62.add(txtFavoritesGetPubID, gbc);
        final JLabel label170 = new JLabel();
        label170.setHorizontalAlignment(4);
        label170.setMaximumSize(new Dimension(400, 25));
        label170.setMinimumSize(new Dimension(400, 25));
        label170.setPreferredSize(new Dimension(400, 25));
        label170.setText("User ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel62.add(label170, gbc);
        final JPanel panel64 = new JPanel();
        panel64.setLayout(new GridBagLayout());
        favoritesOperationPane.addTab("Remove", panel64);
        final JLabel label171 = new JLabel();
        label171.setFont(
                new Font(label171.getFont().getName(), Font.BOLD, label171.getFont().getSize()));
        label171.setHorizontalAlignment(0);
        label171.setMaximumSize(new Dimension(600, 50));
        label171.setMinimumSize(new Dimension(600, 50));
        label171.setPreferredSize(new Dimension(600, 50));
        label171.setText("Removes a photo from a user's favorites list.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel64.add(label171, gbc);
        final JLabel label172 = new JLabel();
        label172.setFont(
                new Font(label172.getFont().getName(), Font.BOLD, label172.getFont().getSize()));
        label172.setHorizontalAlignment(4);
        label172.setMaximumSize(new Dimension(400, 25));
        label172.setMinimumSize(new Dimension(400, 25));
        label172.setPreferredSize(new Dimension(400, 25));
        label172.setText("Photo ID : ");
        label172.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel64.add(label172, gbc);
        txtFavoritesRmvID = new JTextField();
        txtFavoritesRmvID.setMaximumSize(new Dimension(200, 25));
        txtFavoritesRmvID.setMinimumSize(new Dimension(200, 25));
        txtFavoritesRmvID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel64.add(txtFavoritesRmvID, gbc);
        favoritesRmvInvoke = new JButton();
        favoritesRmvInvoke.setFont(new Font(favoritesRmvInvoke.getFont().getName(), Font.BOLD,
                                            favoritesRmvInvoke.getFont().getSize()));
        favoritesRmvInvoke.setMaximumSize(new Dimension(100, 30));
        favoritesRmvInvoke.setMinimumSize(new Dimension(100, 30));
        favoritesRmvInvoke.setPreferredSize(new Dimension(100, 30));
        favoritesRmvInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel64.add(favoritesRmvInvoke, gbc);
        final JScrollPane scrollPane44 = new JScrollPane();
        scrollPane44.setMaximumSize(new Dimension(550, 225));
        scrollPane44.setMinimumSize(new Dimension(550, 225));
        scrollPane44.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel64.add(scrollPane44, gbc);
        favoritesRmvOutput = new JTextArea();
        favoritesRmvOutput.setBackground(new Color(-3355444));
        favoritesRmvOutput.setEditable(false);
        scrollPane44.setViewportView(favoritesRmvOutput);
        final JPanel panel65 = new JPanel();
        panel65.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Geo", panel65);
        geoOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel65.add(geoOperationPane, gbc);
        final JPanel panel66 = new JPanel();
        panel66.setLayout(new GridBagLayout());
        geoOperationPane.addTab("GetLocation ", panel66);
        final JLabel label173 = new JLabel();
        label173.setFont(
                new Font(label173.getFont().getName(), Font.BOLD, label173.getFont().getSize()));
        label173.setHorizontalAlignment(0);
        label173.setMaximumSize(new Dimension(600, 50));
        label173.setMinimumSize(new Dimension(600, 50));
        label173.setPreferredSize(new Dimension(600, 50));
        label173.setText(
                "Get the geo data (latitude and longitude and the accuracy level) for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel66.add(label173, gbc);
        final JLabel label174 = new JLabel();
        label174.setFont(
                new Font(label174.getFont().getName(), Font.BOLD, label174.getFont().getSize()));
        label174.setHorizontalAlignment(4);
        label174.setMaximumSize(new Dimension(400, 25));
        label174.setMinimumSize(new Dimension(400, 25));
        label174.setPreferredSize(new Dimension(400, 25));
        label174.setText("Photo ID : ");
        label174.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel66.add(label174, gbc);
        txtGeoGetLocID = new JTextField();
        txtGeoGetLocID.setMaximumSize(new Dimension(200, 25));
        txtGeoGetLocID.setMinimumSize(new Dimension(200, 25));
        txtGeoGetLocID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel66.add(txtGeoGetLocID, gbc);
        geoGetLocInvoke = new JButton();
        geoGetLocInvoke.setFont(new Font(geoGetLocInvoke.getFont().getName(), Font.BOLD,
                                         geoGetLocInvoke.getFont().getSize()));
        geoGetLocInvoke.setMaximumSize(new Dimension(100, 30));
        geoGetLocInvoke.setMinimumSize(new Dimension(100, 30));
        geoGetLocInvoke.setPreferredSize(new Dimension(100, 30));
        geoGetLocInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel66.add(geoGetLocInvoke, gbc);
        final JScrollPane scrollPane45 = new JScrollPane();
        scrollPane45.setMaximumSize(new Dimension(550, 225));
        scrollPane45.setMinimumSize(new Dimension(550, 225));
        scrollPane45.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel66.add(scrollPane45, gbc);
        geoGetLocOutput = new JTextArea();
        geoGetLocOutput.setBackground(new Color(-3355444));
        geoGetLocOutput.setEditable(false);
        scrollPane45.setViewportView(geoGetLocOutput);
        final JPanel panel67 = new JPanel();
        panel67.setLayout(new GridBagLayout());
        geoOperationPane.addTab("GetPerms", panel67);
        final JLabel label175 = new JLabel();
        label175.setFont(
                new Font(label175.getFont().getName(), Font.BOLD, label175.getFont().getSize()));
        label175.setHorizontalAlignment(0);
        label175.setMaximumSize(new Dimension(600, 50));
        label175.setMinimumSize(new Dimension(600, 50));
        label175.setPreferredSize(new Dimension(600, 50));
        label175.setText("Get permissions for who may view geo data for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel67.add(label175, gbc);
        final JLabel label176 = new JLabel();
        label176.setFont(
                new Font(label176.getFont().getName(), Font.BOLD, label176.getFont().getSize()));
        label176.setHorizontalAlignment(4);
        label176.setMaximumSize(new Dimension(400, 25));
        label176.setMinimumSize(new Dimension(400, 25));
        label176.setPreferredSize(new Dimension(400, 25));
        label176.setText("Photo ID : ");
        label176.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel67.add(label176, gbc);
        txtGeoGetPermsID = new JTextField();
        txtGeoGetPermsID.setMaximumSize(new Dimension(200, 25));
        txtGeoGetPermsID.setMinimumSize(new Dimension(200, 25));
        txtGeoGetPermsID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel67.add(txtGeoGetPermsID, gbc);
        geoGetPermsInvoke = new JButton();
        geoGetPermsInvoke.setFont(new Font(geoGetPermsInvoke.getFont().getName(), Font.BOLD,
                                           geoGetPermsInvoke.getFont().getSize()));
        geoGetPermsInvoke.setMaximumSize(new Dimension(100, 30));
        geoGetPermsInvoke.setMinimumSize(new Dimension(100, 30));
        geoGetPermsInvoke.setPreferredSize(new Dimension(100, 30));
        geoGetPermsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel67.add(geoGetPermsInvoke, gbc);
        final JScrollPane scrollPane46 = new JScrollPane();
        scrollPane46.setMaximumSize(new Dimension(550, 225));
        scrollPane46.setMinimumSize(new Dimension(550, 225));
        scrollPane46.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel67.add(scrollPane46, gbc);
        geoGetPermsOutput = new JTextArea();
        geoGetPermsOutput.setBackground(new Color(-3355444));
        geoGetPermsOutput.setEditable(false);
        scrollPane46.setViewportView(geoGetPermsOutput);
        final JPanel panel68 = new JPanel();
        panel68.setLayout(new GridBagLayout());
        geoOperationPane.addTab("RemoveLocation", panel68);
        final JLabel label177 = new JLabel();
        label177.setFont(
                new Font(label177.getFont().getName(), Font.BOLD, label177.getFont().getSize()));
        label177.setHorizontalAlignment(0);
        label177.setMaximumSize(new Dimension(600, 50));
        label177.setMinimumSize(new Dimension(600, 50));
        label177.setPreferredSize(new Dimension(600, 50));
        label177.setText("Removes the geo data associated with a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel68.add(label177, gbc);
        final JLabel label178 = new JLabel();
        label178.setFont(
                new Font(label178.getFont().getName(), Font.BOLD, label178.getFont().getSize()));
        label178.setHorizontalAlignment(4);
        label178.setMaximumSize(new Dimension(400, 25));
        label178.setMinimumSize(new Dimension(400, 25));
        label178.setPreferredSize(new Dimension(400, 25));
        label178.setText("Photo ID : ");
        label178.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel68.add(label178, gbc);
        txtGeoRmvLocID = new JTextField();
        txtGeoRmvLocID.setMaximumSize(new Dimension(200, 25));
        txtGeoRmvLocID.setMinimumSize(new Dimension(200, 25));
        txtGeoRmvLocID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel68.add(txtGeoRmvLocID, gbc);
        geoRmvLocInvoke = new JButton();
        geoRmvLocInvoke.setFont(new Font(geoRmvLocInvoke.getFont().getName(), Font.BOLD,
                                         geoRmvLocInvoke.getFont().getSize()));
        geoRmvLocInvoke.setMaximumSize(new Dimension(100, 30));
        geoRmvLocInvoke.setMinimumSize(new Dimension(100, 30));
        geoRmvLocInvoke.setPreferredSize(new Dimension(100, 30));
        geoRmvLocInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel68.add(geoRmvLocInvoke, gbc);
        final JScrollPane scrollPane47 = new JScrollPane();
        scrollPane47.setMaximumSize(new Dimension(550, 225));
        scrollPane47.setMinimumSize(new Dimension(550, 225));
        scrollPane47.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel68.add(scrollPane47, gbc);
        geoRmvLocOutput = new JTextArea();
        geoRmvLocOutput.setBackground(new Color(-3355444));
        geoRmvLocOutput.setEditable(false);
        scrollPane47.setViewportView(geoRmvLocOutput);
        final JPanel panel69 = new JPanel();
        panel69.setLayout(new GridBagLayout());
        geoOperationPane.addTab("SetLocation", panel69);
        final JLabel label179 = new JLabel();
        label179.setFont(
                new Font(label179.getFont().getName(), Font.BOLD, label179.getFont().getSize()));
        label179.setHorizontalAlignment(0);
        label179.setMaximumSize(new Dimension(600, 50));
        label179.setMinimumSize(new Dimension(600, 50));
        label179.setPreferredSize(new Dimension(600, 50));
        label179.setText(
                "Sets the geo data (latitude and longitude and, optionally, the accuracy level) for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel69.add(label179, gbc);
        final JLabel label180 = new JLabel();
        label180.setFont(
                new Font(label180.getFont().getName(), Font.BOLD, label180.getFont().getSize()));
        label180.setHorizontalAlignment(4);
        label180.setMaximumSize(new Dimension(400, 25));
        label180.setMinimumSize(new Dimension(400, 25));
        label180.setPreferredSize(new Dimension(400, 25));
        label180.setText("Latitude    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel69.add(label180, gbc);
        final JLabel label181 = new JLabel();
        label181.setHorizontalAlignment(4);
        label181.setMaximumSize(new Dimension(400, 25));
        label181.setMinimumSize(new Dimension(400, 25));
        label181.setPreferredSize(new Dimension(400, 25));
        label181.setText("Accuracy   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel69.add(label181, gbc);
        cmbGeoSetLocAccuracy = new JComboBox();
        cmbGeoSetLocAccuracy.setMaximumSize(new Dimension(200, 25));
        cmbGeoSetLocAccuracy.setMinimumSize(new Dimension(200, 25));
        cmbGeoSetLocAccuracy.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel69.add(cmbGeoSetLocAccuracy, gbc);
        geoSetLocInvoke = new JButton();
        geoSetLocInvoke.setFont(new Font(geoSetLocInvoke.getFont().getName(), Font.BOLD,
                                         geoSetLocInvoke.getFont().getSize()));
        geoSetLocInvoke.setMaximumSize(new Dimension(100, 30));
        geoSetLocInvoke.setMinimumSize(new Dimension(100, 30));
        geoSetLocInvoke.setPreferredSize(new Dimension(100, 30));
        geoSetLocInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel69.add(geoSetLocInvoke, gbc);
        final JScrollPane scrollPane48 = new JScrollPane();
        scrollPane48.setMaximumSize(new Dimension(550, 225));
        scrollPane48.setMinimumSize(new Dimension(550, 225));
        scrollPane48.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel69.add(scrollPane48, gbc);
        geoSetLocOutput = new JTextArea();
        geoSetLocOutput.setBackground(new Color(-3355444));
        scrollPane48.setViewportView(geoSetLocOutput);
        cmbGeoSetLocLatitude = new JComboBox();
        cmbGeoSetLocLatitude.setMaximumSize(new Dimension(200, 25));
        cmbGeoSetLocLatitude.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel14 = new DefaultComboBoxModel();
        cmbGeoSetLocLatitude.setModel(defaultComboBoxModel14);
        cmbGeoSetLocLatitude.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel69.add(cmbGeoSetLocLatitude, gbc);
        final JLabel label182 = new JLabel();
        label182.setFont(
                new Font(label182.getFont().getName(), Font.BOLD, label182.getFont().getSize()));
        label182.setHorizontalAlignment(4);
        label182.setMaximumSize(new Dimension(400, 25));
        label182.setMinimumSize(new Dimension(400, 25));
        label182.setPreferredSize(new Dimension(400, 25));
        label182.setText("Longitude : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel69.add(label182, gbc);
        cmbGeoSetLocLongitude = new JComboBox();
        cmbGeoSetLocLongitude.setMaximumSize(new Dimension(200, 25));
        cmbGeoSetLocLongitude.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel15 = new DefaultComboBoxModel();
        cmbGeoSetLocLongitude.setModel(defaultComboBoxModel15);
        cmbGeoSetLocLongitude.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel69.add(cmbGeoSetLocLongitude, gbc);
        final JLabel label183 = new JLabel();
        label183.setFont(
                new Font(label183.getFont().getName(), Font.BOLD, label183.getFont().getSize()));
        label183.setHorizontalAlignment(4);
        label183.setMaximumSize(new Dimension(400, 25));
        label183.setMinimumSize(new Dimension(400, 25));
        label183.setPreferredSize(new Dimension(400, 25));
        label183.setText("Photo ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel69.add(label183, gbc);
        txtGeoSetLocID = new JTextField();
        txtGeoSetLocID.setMaximumSize(new Dimension(200, 25));
        txtGeoSetLocID.setMinimumSize(new Dimension(200, 25));
        txtGeoSetLocID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel69.add(txtGeoSetLocID, gbc);
        final JPanel panel70 = new JPanel();
        panel70.setLayout(new GridBagLayout());
        geoOperationPane.addTab("SetPerms", panel70);
        final JLabel label184 = new JLabel();
        label184.setFont(
                new Font(label184.getFont().getName(), Font.BOLD, label184.getFont().getSize()));
        label184.setHorizontalAlignment(0);
        label184.setMaximumSize(new Dimension(600, 50));
        label184.setMinimumSize(new Dimension(600, 50));
        label184.setPreferredSize(new Dimension(600, 50));
        label184.setText(
                "Set the permission for who may view the geo data associated with a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel70.add(label184, gbc);
        final JLabel label185 = new JLabel();
        label185.setFont(
                new Font(label185.getFont().getName(), Font.BOLD, label185.getFont().getSize()));
        label185.setHorizontalAlignment(4);
        label185.setMaximumSize(new Dimension(400, 25));
        label185.setMinimumSize(new Dimension(400, 25));
        label185.setPreferredSize(new Dimension(400, 25));
        label185.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel70.add(label185, gbc);
        chkGeoSetPermsPublic = new JCheckBox();
        chkGeoSetPermsPublic.setFont(new Font(chkGeoSetPermsPublic.getFont().getName(), Font.BOLD,
                                              chkGeoSetPermsPublic.getFont().getSize()));
        chkGeoSetPermsPublic.setHorizontalAlignment(4);
        chkGeoSetPermsPublic.setMaximumSize(new Dimension(300, 25));
        chkGeoSetPermsPublic.setMinimumSize(new Dimension(300, 25));
        chkGeoSetPermsPublic.setPreferredSize(new Dimension(300, 25));
        chkGeoSetPermsPublic.setText("Is Public   ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel70.add(chkGeoSetPermsPublic, gbc);
        chkGeoSetPermsFriend = new JCheckBox();
        chkGeoSetPermsFriend.setFont(new Font(chkGeoSetPermsFriend.getFont().getName(), Font.BOLD,
                                              chkGeoSetPermsFriend.getFont().getSize()));
        chkGeoSetPermsFriend.setMaximumSize(new Dimension(200, 25));
        chkGeoSetPermsFriend.setMinimumSize(new Dimension(200, 25));
        chkGeoSetPermsFriend.setPreferredSize(new Dimension(200, 25));
        chkGeoSetPermsFriend.setText("Is Friend");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel70.add(chkGeoSetPermsFriend, gbc);
        final JScrollPane scrollPane49 = new JScrollPane();
        scrollPane49.setMaximumSize(new Dimension(550, 225));
        scrollPane49.setMinimumSize(new Dimension(550, 225));
        scrollPane49.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel70.add(scrollPane49, gbc);
        geoSetPermsOutput = new JTextArea();
        geoSetPermsOutput.setBackground(new Color(-3355444));
        scrollPane49.setViewportView(geoSetPermsOutput);
        geoSetPermsInvoke = new JButton();
        geoSetPermsInvoke.setFont(new Font(geoSetPermsInvoke.getFont().getName(), Font.BOLD,
                                           geoSetPermsInvoke.getFont().getSize()));
        geoSetPermsInvoke.setLabel("Invoke");
        geoSetPermsInvoke.setMaximumSize(new Dimension(100, 30));
        geoSetPermsInvoke.setMinimumSize(new Dimension(100, 30));
        geoSetPermsInvoke.setPreferredSize(new Dimension(100, 30));
        geoSetPermsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel70.add(geoSetPermsInvoke, gbc);
        chkGeoSetPermsContact = new JCheckBox();
        chkGeoSetPermsContact.setFont(new Font(chkGeoSetPermsContact.getFont().getName(), Font.BOLD,
                                               chkGeoSetPermsContact.getFont().getSize()));
        chkGeoSetPermsContact.setHorizontalAlignment(4);
        chkGeoSetPermsContact.setLabel("Is Contact");
        chkGeoSetPermsContact.setMaximumSize(new Dimension(300, 25));
        chkGeoSetPermsContact.setMinimumSize(new Dimension(300, 25));
        chkGeoSetPermsContact.setPreferredSize(new Dimension(300, 25));
        chkGeoSetPermsContact.setText("Is Contact");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel70.add(chkGeoSetPermsContact, gbc);
        txtGeoSetPermsID = new JTextField();
        txtGeoSetPermsID.setMaximumSize(new Dimension(200, 25));
        txtGeoSetPermsID.setMinimumSize(new Dimension(200, 25));
        txtGeoSetPermsID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel70.add(txtGeoSetPermsID, gbc);
        chkGeoSetPermsFamily = new JCheckBox();
        chkGeoSetPermsFamily.setFont(new Font(chkGeoSetPermsFamily.getFont().getName(), Font.BOLD,
                                              chkGeoSetPermsFamily.getFont().getSize()));
        chkGeoSetPermsFamily.setMaximumSize(new Dimension(200, 25));
        chkGeoSetPermsFamily.setMinimumSize(new Dimension(200, 25));
        chkGeoSetPermsFamily.setPreferredSize(new Dimension(200, 25));
        chkGeoSetPermsFamily.setText("Is Family");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel70.add(chkGeoSetPermsFamily, gbc);
        final JPanel panel71 = new JPanel();
        panel71.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Groups", panel71);
        groupsOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel71.add(groupsOperationPane, gbc);
        final JPanel panel72 = new JPanel();
        panel72.setLayout(new GridBagLayout());
        groupsOperationPane.addTab("Browse", panel72);
        final JLabel label186 = new JLabel();
        label186.setFont(
                new Font(label186.getFont().getName(), Font.BOLD, label186.getFont().getSize()));
        label186.setHorizontalAlignment(0);
        label186.setMaximumSize(new Dimension(600, 50));
        label186.setMinimumSize(new Dimension(600, 50));
        label186.setPreferredSize(new Dimension(600, 50));
        label186.setText("Browse the group category tree, finding groups and sub-categories.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel72.add(label186, gbc);
        final JLabel label187 = new JLabel();
        label187.setFont(
                new Font(label187.getFont().getName(), Font.BOLD, label187.getFont().getSize()));
        label187.setHorizontalAlignment(4);
        label187.setMaximumSize(new Dimension(400, 25));
        label187.setMinimumSize(new Dimension(400, 25));
        label187.setPreferredSize(new Dimension(400, 25));
        label187.setText("Category ID : ");
        label187.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel72.add(label187, gbc);
        txtGroupsBrowseID = new JTextField();
        txtGroupsBrowseID.setMaximumSize(new Dimension(200, 25));
        txtGroupsBrowseID.setMinimumSize(new Dimension(200, 25));
        txtGroupsBrowseID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel72.add(txtGroupsBrowseID, gbc);
        groupsBrowseInvoke = new JButton();
        groupsBrowseInvoke.setFont(new Font(groupsBrowseInvoke.getFont().getName(), Font.BOLD,
                                            groupsBrowseInvoke.getFont().getSize()));
        groupsBrowseInvoke.setMaximumSize(new Dimension(100, 30));
        groupsBrowseInvoke.setMinimumSize(new Dimension(100, 30));
        groupsBrowseInvoke.setPreferredSize(new Dimension(100, 30));
        groupsBrowseInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel72.add(groupsBrowseInvoke, gbc);
        final JScrollPane scrollPane50 = new JScrollPane();
        scrollPane50.setMaximumSize(new Dimension(550, 225));
        scrollPane50.setMinimumSize(new Dimension(550, 225));
        scrollPane50.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel72.add(scrollPane50, gbc);
        groupsBrowseOutput = new JTextArea();
        groupsBrowseOutput.setBackground(new Color(-3355444));
        groupsBrowseOutput.setEditable(false);
        scrollPane50.setViewportView(groupsBrowseOutput);
        final JPanel panel73 = new JPanel();
        panel73.setLayout(new GridBagLayout());
        groupsOperationPane.addTab("GetInfo", panel73);
        final JLabel label188 = new JLabel();
        label188.setFont(
                new Font(label188.getFont().getName(), Font.BOLD, label188.getFont().getSize()));
        label188.setHorizontalAlignment(0);
        label188.setMaximumSize(new Dimension(600, 50));
        label188.setMinimumSize(new Dimension(600, 50));
        label188.setPreferredSize(new Dimension(600, 50));
        label188.setText("Get information about a group.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel73.add(label188, gbc);
        final JLabel label189 = new JLabel();
        label189.setFont(
                new Font(label189.getFont().getName(), Font.BOLD, label189.getFont().getSize()));
        label189.setHorizontalAlignment(4);
        label189.setMaximumSize(new Dimension(400, 25));
        label189.setMinimumSize(new Dimension(400, 25));
        label189.setPreferredSize(new Dimension(400, 25));
        label189.setText("Group ID : ");
        label189.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel73.add(label189, gbc);
        txtGroupsGetInfoID = new JTextField();
        txtGroupsGetInfoID.setMaximumSize(new Dimension(200, 25));
        txtGroupsGetInfoID.setMinimumSize(new Dimension(200, 25));
        txtGroupsGetInfoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel73.add(txtGroupsGetInfoID, gbc);
        groupsGetInfoInvoke = new JButton();
        groupsGetInfoInvoke.setFont(new Font(groupsGetInfoInvoke.getFont().getName(), Font.BOLD,
                                             groupsGetInfoInvoke.getFont().getSize()));
        groupsGetInfoInvoke.setMaximumSize(new Dimension(100, 30));
        groupsGetInfoInvoke.setMinimumSize(new Dimension(100, 30));
        groupsGetInfoInvoke.setPreferredSize(new Dimension(100, 30));
        groupsGetInfoInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel73.add(groupsGetInfoInvoke, gbc);
        final JScrollPane scrollPane51 = new JScrollPane();
        scrollPane51.setMaximumSize(new Dimension(550, 225));
        scrollPane51.setMinimumSize(new Dimension(550, 225));
        scrollPane51.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel73.add(scrollPane51, gbc);
        groupsGetInfoOutput = new JTextArea();
        groupsGetInfoOutput.setBackground(new Color(-3355444));
        groupsGetInfoOutput.setEditable(false);
        scrollPane51.setViewportView(groupsGetInfoOutput);
        final JPanel panel74 = new JPanel();
        panel74.setLayout(new GridBagLayout());
        groupsOperationPane.addTab("Search", panel74);
        final JLabel label190 = new JLabel();
        label190.setFont(
                new Font(label190.getFont().getName(), Font.BOLD, label190.getFont().getSize()));
        label190.setHorizontalAlignment(0);
        label190.setMaximumSize(new Dimension(800, 50));
        label190.setMinimumSize(new Dimension(800, 50));
        label190.setPreferredSize(new Dimension(800, 50));
        label190.setText(
                "Search for groups. 18+ groups will only be returned for authenticated calls where the authenticated user is over 18.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel74.add(label190, gbc);
        final JLabel label191 = new JLabel();
        label191.setFont(new Font(label191.getFont().getName(), label191.getFont().getStyle(),
                                  label191.getFont().getSize()));
        label191.setHorizontalAlignment(4);
        label191.setMaximumSize(new Dimension(400, 25));
        label191.setMinimumSize(new Dimension(400, 25));
        label191.setPreferredSize(new Dimension(400, 25));
        label191.setText("Text        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel74.add(label191, gbc);
        final JLabel label192 = new JLabel();
        label192.setHorizontalAlignment(4);
        label192.setMaximumSize(new Dimension(400, 25));
        label192.setMinimumSize(new Dimension(400, 25));
        label192.setPreferredSize(new Dimension(400, 25));
        label192.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel74.add(label192, gbc);
        txtGroupsSearchPage = new JTextField();
        txtGroupsSearchPage.setMaximumSize(new Dimension(200, 25));
        txtGroupsSearchPage.setMinimumSize(new Dimension(200, 25));
        txtGroupsSearchPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel74.add(txtGroupsSearchPage, gbc);
        final JLabel label193 = new JLabel();
        label193.setHorizontalAlignment(4);
        label193.setMaximumSize(new Dimension(400, 25));
        label193.setMinimumSize(new Dimension(400, 25));
        label193.setPreferredSize(new Dimension(400, 25));
        label193.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel74.add(label193, gbc);
        cmbGroupsSearchPerPage = new JComboBox();
        cmbGroupsSearchPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGroupsSearchPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGroupsSearchPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel74.add(cmbGroupsSearchPerPage, gbc);
        groupsSearchInvoke = new JButton();
        groupsSearchInvoke.setFont(new Font(groupsSearchInvoke.getFont().getName(), Font.BOLD,
                                            groupsSearchInvoke.getFont().getSize()));
        groupsSearchInvoke.setMaximumSize(new Dimension(100, 30));
        groupsSearchInvoke.setMinimumSize(new Dimension(100, 30));
        groupsSearchInvoke.setPreferredSize(new Dimension(100, 30));
        groupsSearchInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel74.add(groupsSearchInvoke, gbc);
        final JScrollPane scrollPane52 = new JScrollPane();
        scrollPane52.setMaximumSize(new Dimension(550, 225));
        scrollPane52.setMinimumSize(new Dimension(550, 225));
        scrollPane52.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel74.add(scrollPane52, gbc);
        groupsSearchOutput = new JTextArea();
        groupsSearchOutput.setBackground(new Color(-3355444));
        scrollPane52.setViewportView(groupsSearchOutput);
        txtGroupsSearchText = new JTextField();
        txtGroupsSearchText.setMaximumSize(new Dimension(200, 25));
        txtGroupsSearchText.setMinimumSize(new Dimension(200, 25));
        txtGroupsSearchText.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel74.add(txtGroupsSearchText, gbc);
        final JPanel panel75 = new JPanel();
        panel75.setLayout(new GridBagLayout());
        tabbedPane1.addTab("GroupsPools", panel75);
        tabbedPane2 = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel75.add(tabbedPane2, gbc);
        final JPanel panel76 = new JPanel();
        panel76.setLayout(new GridBagLayout());
        tabbedPane2.addTab("Add", panel76);
        final JLabel label194 = new JLabel();
        label194.setFont(
                new Font(label194.getFont().getName(), Font.BOLD, label194.getFont().getSize()));
        label194.setHorizontalAlignment(0);
        label194.setMaximumSize(new Dimension(600, 50));
        label194.setMinimumSize(new Dimension(600, 50));
        label194.setPreferredSize(new Dimension(600, 50));
        label194.setText("Add a photo to a group's pool.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel76.add(label194, gbc);
        final JLabel label195 = new JLabel();
        label195.setFont(
                new Font(label195.getFont().getName(), Font.BOLD, label195.getFont().getSize()));
        label195.setHorizontalAlignment(4);
        label195.setMaximumSize(new Dimension(400, 25));
        label195.setMinimumSize(new Dimension(400, 25));
        label195.setPreferredSize(new Dimension(400, 25));
        label195.setText("Photo ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel76.add(label195, gbc);
        final JLabel label196 = new JLabel();
        label196.setFont(
                new Font(label196.getFont().getName(), Font.BOLD, label196.getFont().getSize()));
        label196.setHorizontalAlignment(4);
        label196.setMaximumSize(new Dimension(400, 25));
        label196.setMinimumSize(new Dimension(400, 25));
        label196.setPreferredSize(new Dimension(400, 25));
        label196.setText("Group ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel76.add(label196, gbc);
        txtGrpPoolsAddGroupID = new JTextField();
        txtGrpPoolsAddGroupID.setMaximumSize(new Dimension(200, 25));
        txtGrpPoolsAddGroupID.setMinimumSize(new Dimension(200, 25));
        txtGrpPoolsAddGroupID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel76.add(txtGrpPoolsAddGroupID, gbc);
        grpPoolsAddInvoke = new JButton();
        grpPoolsAddInvoke.setFont(new Font(grpPoolsAddInvoke.getFont().getName(), Font.BOLD,
                                           grpPoolsAddInvoke.getFont().getSize()));
        grpPoolsAddInvoke.setMaximumSize(new Dimension(100, 30));
        grpPoolsAddInvoke.setMinimumSize(new Dimension(100, 30));
        grpPoolsAddInvoke.setPreferredSize(new Dimension(100, 30));
        grpPoolsAddInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel76.add(grpPoolsAddInvoke, gbc);
        final JScrollPane scrollPane53 = new JScrollPane();
        scrollPane53.setMaximumSize(new Dimension(550, 225));
        scrollPane53.setMinimumSize(new Dimension(550, 225));
        scrollPane53.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel76.add(scrollPane53, gbc);
        grpPoolsAddOutput = new JTextArea();
        grpPoolsAddOutput.setBackground(new Color(-3355444));
        scrollPane53.setViewportView(grpPoolsAddOutput);
        txtGrpPoolsAddPhotoID = new JTextField();
        txtGrpPoolsAddPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGrpPoolsAddPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGrpPoolsAddPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel76.add(txtGrpPoolsAddPhotoID, gbc);
        final JPanel panel77 = new JPanel();
        panel77.setLayout(new GridBagLayout());
        tabbedPane2.addTab("GetContext", panel77);
        final JLabel label197 = new JLabel();
        label197.setFont(
                new Font(label197.getFont().getName(), Font.BOLD, label197.getFont().getSize()));
        label197.setHorizontalAlignment(0);
        label197.setMaximumSize(new Dimension(600, 50));
        label197.setMinimumSize(new Dimension(600, 50));
        label197.setPreferredSize(new Dimension(600, 50));
        label197.setText("Returns next and previous photos for a photo in a group pool.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel77.add(label197, gbc);
        final JLabel label198 = new JLabel();
        label198.setFont(
                new Font(label198.getFont().getName(), Font.BOLD, label198.getFont().getSize()));
        label198.setHorizontalAlignment(4);
        label198.setMaximumSize(new Dimension(400, 25));
        label198.setMinimumSize(new Dimension(400, 25));
        label198.setPreferredSize(new Dimension(400, 25));
        label198.setText("Photo ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel77.add(label198, gbc);
        final JLabel label199 = new JLabel();
        label199.setFont(
                new Font(label199.getFont().getName(), Font.BOLD, label199.getFont().getSize()));
        label199.setHorizontalAlignment(4);
        label199.setMaximumSize(new Dimension(400, 25));
        label199.setMinimumSize(new Dimension(400, 25));
        label199.setPreferredSize(new Dimension(400, 25));
        label199.setText("Group ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel77.add(label199, gbc);
        txtGrpPoolsContextGrpID = new JTextField();
        txtGrpPoolsContextGrpID.setMaximumSize(new Dimension(200, 25));
        txtGrpPoolsContextGrpID.setMinimumSize(new Dimension(200, 25));
        txtGrpPoolsContextGrpID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel77.add(txtGrpPoolsContextGrpID, gbc);
        grpPoolsContextInvoke = new JButton();
        grpPoolsContextInvoke.setFont(new Font(grpPoolsContextInvoke.getFont().getName(), Font.BOLD,
                                               grpPoolsContextInvoke.getFont().getSize()));
        grpPoolsContextInvoke.setMaximumSize(new Dimension(100, 30));
        grpPoolsContextInvoke.setMinimumSize(new Dimension(100, 30));
        grpPoolsContextInvoke.setPreferredSize(new Dimension(100, 30));
        grpPoolsContextInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel77.add(grpPoolsContextInvoke, gbc);
        final JScrollPane scrollPane54 = new JScrollPane();
        scrollPane54.setMaximumSize(new Dimension(550, 225));
        scrollPane54.setMinimumSize(new Dimension(550, 225));
        scrollPane54.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel77.add(scrollPane54, gbc);
        grpPoolsContextOutput = new JTextArea();
        grpPoolsContextOutput.setBackground(new Color(-3355444));
        scrollPane54.setViewportView(grpPoolsContextOutput);
        txtGrpPoolsContextPhotoID = new JTextField();
        txtGrpPoolsContextPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGrpPoolsContextPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGrpPoolsContextPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel77.add(txtGrpPoolsContextPhotoID, gbc);
        final JPanel panel78 = new JPanel();
        panel78.setLayout(new GridBagLayout());
        tabbedPane2.addTab("GetGroups", panel78);
        final JLabel label200 = new JLabel();
        label200.setFont(
                new Font(label200.getFont().getName(), Font.BOLD, label200.getFont().getSize()));
        label200.setHorizontalAlignment(0);
        label200.setMaximumSize(new Dimension(800, 50));
        label200.setMinimumSize(new Dimension(800, 50));
        label200.setPreferredSize(new Dimension(800, 50));
        label200.setText("Returns a list of groups to which you can add photos.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel78.add(label200, gbc);
        final JLabel label201 = new JLabel();
        label201.setHorizontalAlignment(4);
        label201.setMaximumSize(new Dimension(400, 25));
        label201.setMinimumSize(new Dimension(400, 25));
        label201.setPreferredSize(new Dimension(400, 25));
        label201.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel78.add(label201, gbc);
        txtGrpPoolsGrpsPage = new JTextField();
        txtGrpPoolsGrpsPage.setMaximumSize(new Dimension(200, 25));
        txtGrpPoolsGrpsPage.setMinimumSize(new Dimension(200, 25));
        txtGrpPoolsGrpsPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel78.add(txtGrpPoolsGrpsPage, gbc);
        final JLabel label202 = new JLabel();
        label202.setHorizontalAlignment(4);
        label202.setMaximumSize(new Dimension(400, 25));
        label202.setMinimumSize(new Dimension(400, 25));
        label202.setPreferredSize(new Dimension(400, 25));
        label202.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel78.add(label202, gbc);
        cmbGrpPoolsGrpsPerPage = new JComboBox();
        cmbGrpPoolsGrpsPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGrpPoolsGrpsPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGrpPoolsGrpsPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel78.add(cmbGrpPoolsGrpsPerPage, gbc);
        grpPoolsGrpsInvoke = new JButton();
        grpPoolsGrpsInvoke.setFont(new Font(grpPoolsGrpsInvoke.getFont().getName(), Font.BOLD,
                                            grpPoolsGrpsInvoke.getFont().getSize()));
        grpPoolsGrpsInvoke.setMaximumSize(new Dimension(100, 30));
        grpPoolsGrpsInvoke.setMinimumSize(new Dimension(100, 30));
        grpPoolsGrpsInvoke.setPreferredSize(new Dimension(100, 30));
        grpPoolsGrpsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel78.add(grpPoolsGrpsInvoke, gbc);
        final JScrollPane scrollPane55 = new JScrollPane();
        scrollPane55.setMaximumSize(new Dimension(550, 225));
        scrollPane55.setMinimumSize(new Dimension(550, 225));
        scrollPane55.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel78.add(scrollPane55, gbc);
        grpPoolsGrpsOutput = new JTextArea();
        grpPoolsGrpsOutput.setBackground(new Color(-3355444));
        scrollPane55.setViewportView(grpPoolsGrpsOutput);
        findByEmailOutput.setNextFocusableComponent(tabbedPane1);
        blogsGetListOutput.setNextFocusableComponent(tabbedPane1);
    }

    public void populateOperationPane() {
        GridBagConstraints gbc;
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Photos", panel10);
        photosOperationPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel10.add(photosOperationPane, gbc);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetInfo", panel11);
        txtPhotosGetInfoPhotoID = new JTextField();
        txtPhotosGetInfoPhotoID.setMaximumSize(new Dimension(200, 25));
        txtPhotosGetInfoPhotoID.setMinimumSize(new Dimension(200, 25));
        txtPhotosGetInfoPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel11.add(txtPhotosGetInfoPhotoID, gbc);
        final JLabel label12 = new JLabel();
        label12.setFont(
                new Font(label12.getFont().getName(), Font.BOLD, label12.getFont().getSize()));
        label12.setHorizontalAlignment(4);
        label12.setMaximumSize(new Dimension(400, 25));
        label12.setMinimumSize(new Dimension(400, 25));
        label12.setPreferredSize(new Dimension(400, 25));
        label12.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel11.add(label12, gbc);
        txtPhotosGetInfoSecret = new JTextField();
        txtPhotosGetInfoSecret.setMaximumSize(new Dimension(200, 25));
        txtPhotosGetInfoSecret.setMinimumSize(new Dimension(200, 25));
        txtPhotosGetInfoSecret.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel11.add(txtPhotosGetInfoSecret, gbc);
        final JLabel label13 = new JLabel();
        label13.setHorizontalAlignment(4);
        label13.setMaximumSize(new Dimension(400, 25));
        label13.setMinimumSize(new Dimension(400, 25));
        label13.setPreferredSize(new Dimension(400, 25));
        label13.setText("Secret     : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel11.add(label13, gbc);
        photosGetInfoInvoke = new JButton();
        photosGetInfoInvoke.setFont(new Font(photosGetInfoInvoke.getFont().getName(), Font.BOLD,
                                             photosGetInfoInvoke.getFont().getSize()));
        photosGetInfoInvoke.setLabel("Invoke");
        photosGetInfoInvoke.setMaximumSize(new Dimension(100, 30));
        photosGetInfoInvoke.setMinimumSize(new Dimension(100, 30));
        photosGetInfoInvoke.setPreferredSize(new Dimension(100, 30));
        photosGetInfoInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel11.add(photosGetInfoInvoke, gbc);
        final JScrollPane scrollPane7 = new JScrollPane();
        scrollPane7.setMaximumSize(new Dimension(550, 225));
        scrollPane7.setMinimumSize(new Dimension(550, 225));
        scrollPane7.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel11.add(scrollPane7, gbc);
        photosGetInfoOutput = new JTextArea();
        photosGetInfoOutput.setEditable(false);
        scrollPane7.setViewportView(photosGetInfoOutput);
        final JLabel label14 = new JLabel();
        label14.setFont(
                new Font(label14.getFont().getName(), Font.BOLD, label14.getFont().getSize()));
        label14.setHorizontalAlignment(0);
        label14.setMaximumSize(new Dimension(600, 50));
        label14.setMinimumSize(new Dimension(600, 50));
        label14.setPreferredSize(new Dimension(600, 50));
        label14.setText(
                "Get information about a photo. The calling user must have permission to view the photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel11.add(label14, gbc);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        photosOperationPane.addTab("AddTags", panel12);
        final JLabel label15 = new JLabel();
        label15.setFont(
                new Font(label15.getFont().getName(), Font.BOLD, label15.getFont().getSize()));
        label15.setHorizontalAlignment(0);
        label15.setMaximumSize(new Dimension(600, 50));
        label15.setMinimumSize(new Dimension(600, 50));
        label15.setPreferredSize(new Dimension(600, 50));
        label15.setText("Add tags to a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel12.add(label15, gbc);
        final JLabel label16 = new JLabel();
        label16.setFont(
                new Font(label16.getFont().getName(), Font.BOLD, label16.getFont().getSize()));
        label16.setHorizontalAlignment(4);
        label16.setMaximumSize(new Dimension(400, 25));
        label16.setMinimumSize(new Dimension(400, 25));
        label16.setPreferredSize(new Dimension(400, 25));
        label16.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel12.add(label16, gbc);
        txtPhotosAddTagsPhotoID = new JTextField();
        txtPhotosAddTagsPhotoID.setMaximumSize(new Dimension(200, 25));
        txtPhotosAddTagsPhotoID.setMinimumSize(new Dimension(200, 25));
        txtPhotosAddTagsPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel12.add(txtPhotosAddTagsPhotoID, gbc);
        final JLabel label17 = new JLabel();
        label17.setFont(
                new Font(label17.getFont().getName(), Font.BOLD, label17.getFont().getSize()));
        label17.setHorizontalAlignment(4);
        label17.setMaximumSize(new Dimension(400, 25));
        label17.setMinimumSize(new Dimension(400, 25));
        label17.setPreferredSize(new Dimension(400, 25));
        label17.setText("Tags       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel12.add(label17, gbc);
        txtAddTags = new JTextField();
        txtAddTags.setMaximumSize(new Dimension(200, 25));
        txtAddTags.setMinimumSize(new Dimension(200, 25));
        txtAddTags.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel12.add(txtAddTags, gbc);
        final JScrollPane scrollPane8 = new JScrollPane();
        scrollPane8.setBackground(new Color(-3355444));
        scrollPane8.setMaximumSize(new Dimension(550, 225));
        scrollPane8.setMinimumSize(new Dimension(550, 225));
        scrollPane8.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel12.add(scrollPane8, gbc);
        photosAddTagsOutput = new JTextArea();
        photosAddTagsOutput.setBackground(new Color(-3355444));
        photosAddTagsOutput.setEditable(false);
        scrollPane8.setViewportView(photosAddTagsOutput);
        photosAddTagsInvoke = new JButton();
        photosAddTagsInvoke.setFont(new Font(photosAddTagsInvoke.getFont().getName(), Font.BOLD,
                                             photosAddTagsInvoke.getFont().getSize()));
        photosAddTagsInvoke.setMaximumSize(new Dimension(100, 30));
        photosAddTagsInvoke.setMinimumSize(new Dimension(100, 30));
        photosAddTagsInvoke.setPreferredSize(new Dimension(100, 30));
        photosAddTagsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel12.add(photosAddTagsInvoke, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        photosOperationPane.addTab("Delete", panel13);
        final JLabel label18 = new JLabel();
        label18.setFont(
                new Font(label18.getFont().getName(), Font.BOLD, label18.getFont().getSize()));
        label18.setHorizontalAlignment(0);
        label18.setMaximumSize(new Dimension(600, 50));
        label18.setMinimumSize(new Dimension(600, 50));
        label18.setPreferredSize(new Dimension(600, 50));
        label18.setText("Delete a photo from flickr.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel13.add(label18, gbc);
        final JLabel label19 = new JLabel();
        label19.setFont(
                new Font(label19.getFont().getName(), Font.BOLD, label19.getFont().getSize()));
        label19.setHorizontalAlignment(4);
        label19.setMaximumSize(new Dimension(400, 25));
        label19.setMinimumSize(new Dimension(400, 25));
        label19.setPreferredSize(new Dimension(400, 25));
        label19.setText("Photo ID : ");
        label19.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel13.add(label19, gbc);
        txtPhotosDeletePhotoID = new JTextField();
        txtPhotosDeletePhotoID.setMaximumSize(new Dimension(200, 25));
        txtPhotosDeletePhotoID.setMinimumSize(new Dimension(200, 25));
        txtPhotosDeletePhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel13.add(txtPhotosDeletePhotoID, gbc);
        photosDeleteInvoke = new JButton();
        photosDeleteInvoke.setFont(new Font(photosDeleteInvoke.getFont().getName(), Font.BOLD,
                                            photosDeleteInvoke.getFont().getSize()));
        photosDeleteInvoke.setMaximumSize(new Dimension(100, 30));
        photosDeleteInvoke.setMinimumSize(new Dimension(100, 30));
        photosDeleteInvoke.setPreferredSize(new Dimension(100, 30));
        photosDeleteInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel13.add(photosDeleteInvoke, gbc);
        final JScrollPane scrollPane9 = new JScrollPane();
        scrollPane9.setMaximumSize(new Dimension(550, 225));
        scrollPane9.setMinimumSize(new Dimension(550, 225));
        scrollPane9.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel13.add(scrollPane9, gbc);
        photosDeleteOutput = new JTextArea();
        photosDeleteOutput.setBackground(new Color(-3355444));
        photosDeleteOutput.setEditable(false);
        scrollPane9.setViewportView(photosDeleteOutput);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetAllContexts ", panel14);
        final JLabel label20 = new JLabel();
        label20.setFont(
                new Font(label20.getFont().getName(), Font.BOLD, label20.getFont().getSize()));
        label20.setHorizontalAlignment(0);
        label20.setMaximumSize(new Dimension(600, 50));
        label20.setMinimumSize(new Dimension(600, 50));
        label20.setPreferredSize(new Dimension(600, 50));
        label20.setText("Returns all visible sets and pools the photo belongs to.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel14.add(label20, gbc);
        final JLabel label21 = new JLabel();
        label21.setFont(
                new Font(label21.getFont().getName(), Font.BOLD, label21.getFont().getSize()));
        label21.setHorizontalAlignment(4);
        label21.setMaximumSize(new Dimension(400, 25));
        label21.setMinimumSize(new Dimension(400, 25));
        label21.setPreferredSize(new Dimension(400, 25));
        label21.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel14.add(label21, gbc);
        txtGetAllContextsPhotoID = new JTextField();
        txtGetAllContextsPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGetAllContextsPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGetAllContextsPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel14.add(txtGetAllContextsPhotoID, gbc);
        getAllContextsInvoke = new JButton();
        getAllContextsInvoke.setFont(new Font(getAllContextsInvoke.getFont().getName(), Font.BOLD,
                                              getAllContextsInvoke.getFont().getSize()));
        getAllContextsInvoke.setMaximumSize(new Dimension(100, 30));
        getAllContextsInvoke.setMinimumSize(new Dimension(100, 30));
        getAllContextsInvoke.setPreferredSize(new Dimension(100, 30));
        getAllContextsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel14.add(getAllContextsInvoke, gbc);
        final JScrollPane scrollPane10 = new JScrollPane();
        scrollPane10.setMaximumSize(new Dimension(550, 225));
        scrollPane10.setMinimumSize(new Dimension(550, 225));
        scrollPane10.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel14.add(scrollPane10, gbc);
        getAllContextsOutput = new JTextArea();
        getAllContextsOutput.setBackground(new Color(-3355444));
        scrollPane10.setViewportView(getAllContextsOutput);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetContactsPhotos", panel15);
        final JLabel label22 = new JLabel();
        label22.setFont(
                new Font(label22.getFont().getName(), Font.BOLD, label22.getFont().getSize()));
        label22.setHorizontalAlignment(0);
        label22.setMaximumSize(new Dimension(600, 50));
        label22.setMinimumSize(new Dimension(600, 50));
        label22.setPreferredSize(new Dimension(600, 50));
        label22.setText("Fetch a list of recent photos from the calling users' contacts.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel15.add(label22, gbc);
        final JLabel label23 = new JLabel();
        label23.setFont(new Font(label23.getFont().getName(), label23.getFont().getStyle(),
                                 label23.getFont().getSize()));
        label23.setHorizontalAlignment(4);
        label23.setMaximumSize(new Dimension(400, 25));
        label23.setMinimumSize(new Dimension(400, 25));
        label23.setPreferredSize(new Dimension(400, 25));
        label23.setText("Count : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel15.add(label23, gbc);
        chkGetContactsPhotosFriends = new JCheckBox();
        chkGetContactsPhotosFriends.setHorizontalAlignment(4);
        chkGetContactsPhotosFriends.setMaximumSize(new Dimension(300, 25));
        chkGetContactsPhotosFriends.setMinimumSize(new Dimension(300, 25));
        chkGetContactsPhotosFriends.setPreferredSize(new Dimension(300, 25));
        chkGetContactsPhotosFriends.setText("Just Friends");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel15.add(chkGetContactsPhotosFriends, gbc);
        chkGetContactsPhotosSingle = new JCheckBox();
        chkGetContactsPhotosSingle.setMaximumSize(new Dimension(200, 25));
        chkGetContactsPhotosSingle.setMinimumSize(new Dimension(200, 25));
        chkGetContactsPhotosSingle.setPreferredSize(new Dimension(200, 25));
        chkGetContactsPhotosSingle.setText("Single Photo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel15.add(chkGetContactsPhotosSingle, gbc);
        final JScrollPane scrollPane11 = new JScrollPane();
        scrollPane11.setMaximumSize(new Dimension(550, 225));
        scrollPane11.setMinimumSize(new Dimension(550, 225));
        scrollPane11.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel15.add(scrollPane11, gbc);
        getContactsPhotosOutput = new JTextArea();
        getContactsPhotosOutput.setBackground(new Color(-3355444));
        scrollPane11.setViewportView(getContactsPhotosOutput);
        getContactsPhotosInvoke = new JButton();
        getContactsPhotosInvoke.setFont(new Font(getContactsPhotosInvoke.getFont().getName(),
                                                 Font.BOLD,
                                                 getContactsPhotosInvoke.getFont().getSize()));
        getContactsPhotosInvoke.setLabel("Invoke");
        getContactsPhotosInvoke.setMaximumSize(new Dimension(100, 30));
        getContactsPhotosInvoke.setMinimumSize(new Dimension(100, 30));
        getContactsPhotosInvoke.setPreferredSize(new Dimension(100, 30));
        getContactsPhotosInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel15.add(getContactsPhotosInvoke, gbc);
        cmbGetContactsPhotos = new JComboBox();
        cmbGetContactsPhotos.setMaximumSize(new Dimension(200, 25));
        cmbGetContactsPhotos.setMinimumSize(new Dimension(200, 25));
        cmbGetContactsPhotos.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel15.add(cmbGetContactsPhotos, gbc);
        chkGetContactsPhotosSelf = new JCheckBox();
        chkGetContactsPhotosSelf.setHorizontalAlignment(4);
        chkGetContactsPhotosSelf.setLabel("Include Self ");
        chkGetContactsPhotosSelf.setMaximumSize(new Dimension(300, 25));
        chkGetContactsPhotosSelf.setMinimumSize(new Dimension(300, 25));
        chkGetContactsPhotosSelf.setPreferredSize(new Dimension(300, 25));
        chkGetContactsPhotosSelf.setText("Include Self ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel15.add(chkGetContactsPhotosSelf, gbc);
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel15.add(panel16, gbc);
        final JLabel label24 = new JLabel();
        label24.setFont(
                new Font(label24.getFont().getName(), Font.BOLD, label24.getFont().getSize()));
        label24.setHorizontalAlignment(0);
        label24.setMaximumSize(new Dimension(400, 25));
        label24.setMinimumSize(new Dimension(400, 25));
        label24.setPreferredSize(new Dimension(400, 25));
        label24.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel16.add(label24, gbc);
        chkGetContactsPhotosLicense = new JCheckBox();
        chkGetContactsPhotosLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel16.add(chkGetContactsPhotosLicense, gbc);
        chkGetContactsPhotosUploadDate = new JCheckBox();
        chkGetContactsPhotosUploadDate.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel16.add(chkGetContactsPhotosUploadDate, gbc);
        chkGetContactsPhotosDateTaken = new JCheckBox();
        chkGetContactsPhotosDateTaken.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel16.add(chkGetContactsPhotosDateTaken, gbc);
        chkGetContactsPhotosOwner = new JCheckBox();
        chkGetContactsPhotosOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel16.add(chkGetContactsPhotosOwner, gbc);
        chkGetContactsPhotosServer = new JCheckBox();
        chkGetContactsPhotosServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel16.add(chkGetContactsPhotosServer, gbc);
        chkGetContactsPhotosOriginal = new JCheckBox();
        chkGetContactsPhotosOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel16.add(chkGetContactsPhotosOriginal, gbc);
        chkGetContactsPhotosLastUpdate = new JCheckBox();
        chkGetContactsPhotosLastUpdate.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel16.add(chkGetContactsPhotosLastUpdate, gbc);
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetContactsPublicPhotos", panel17);
        final JLabel label25 = new JLabel();
        label25.setFont(
                new Font(label25.getFont().getName(), Font.BOLD, label25.getFont().getSize()));
        label25.setHorizontalAlignment(0);
        label25.setMaximumSize(new Dimension(600, 50));
        label25.setMinimumSize(new Dimension(600, 50));
        label25.setPreferredSize(new Dimension(600, 50));
        label25.setText("Fetch a list of recent public photos from a users' contacts.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel17.add(label25, gbc);
        final JLabel label26 = new JLabel();
        label26.setFont(
                new Font(label26.getFont().getName(), Font.BOLD, label26.getFont().getSize()));
        label26.setHorizontalAlignment(4);
        label26.setMaximumSize(new Dimension(400, 25));
        label26.setMinimumSize(new Dimension(400, 25));
        label26.setPreferredSize(new Dimension(400, 25));
        label26.setText("User ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel17.add(label26, gbc);
        txtGetContactsPublicPhotosUserID = new JTextField();
        txtGetContactsPublicPhotosUserID.setMaximumSize(new Dimension(200, 25));
        txtGetContactsPublicPhotosUserID.setMinimumSize(new Dimension(200, 25));
        txtGetContactsPublicPhotosUserID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel17.add(txtGetContactsPublicPhotosUserID, gbc);
        final JLabel label27 = new JLabel();
        label27.setFont(new Font(label27.getFont().getName(), label27.getFont().getStyle(),
                                 label27.getFont().getSize()));
        label27.setHorizontalAlignment(4);
        label27.setMaximumSize(new Dimension(400, 25));
        label27.setMinimumSize(new Dimension(400, 25));
        label27.setPreferredSize(new Dimension(400, 25));
        label27.setText("Count   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel17.add(label27, gbc);
        cmbGetContactsPublicPhotosCount = new JComboBox();
        cmbGetContactsPublicPhotosCount.setMaximumSize(new Dimension(200, 25));
        cmbGetContactsPublicPhotosCount.setMinimumSize(new Dimension(200, 25));
        cmbGetContactsPublicPhotosCount.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel17.add(cmbGetContactsPublicPhotosCount, gbc);
        chkGetContactsPublicPhotosSingle = new JCheckBox();
        chkGetContactsPublicPhotosSingle.setMaximumSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosSingle.setMinimumSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosSingle.setPreferredSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosSingle.setText("Single Photo");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel17.add(chkGetContactsPublicPhotosSingle, gbc);
        chkGetContactsPublicPhotosSelf = new JCheckBox();
        chkGetContactsPublicPhotosSelf.setHorizontalAlignment(4);
        chkGetContactsPublicPhotosSelf.setMaximumSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosSelf.setMinimumSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosSelf.setPreferredSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosSelf.setText("Include Self ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel17.add(chkGetContactsPublicPhotosSelf, gbc);
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel17.add(panel18, gbc);
        final JLabel label28 = new JLabel();
        label28.setFont(
                new Font(label28.getFont().getName(), Font.BOLD, label28.getFont().getSize()));
        label28.setMaximumSize(new Dimension(400, 25));
        label28.setMinimumSize(new Dimension(400, 25));
        label28.setPreferredSize(new Dimension(400, 25));
        label28.setText("Extra information to fetch for each returned record.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(label28, gbc);
        licenseCheckBox = new JCheckBox();
        licenseCheckBox.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(licenseCheckBox, gbc);
        owner_nameCheckBox = new JCheckBox();
        owner_nameCheckBox.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(owner_nameCheckBox, gbc);
        date_takenCheckBox = new JCheckBox();
        date_takenCheckBox.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(date_takenCheckBox, gbc);
        date_uploadCheckBox = new JCheckBox();
        date_uploadCheckBox.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(date_uploadCheckBox, gbc);
        icon_serverCheckBox = new JCheckBox();
        icon_serverCheckBox.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(icon_serverCheckBox, gbc);
        original_formatCheckBox = new JCheckBox();
        original_formatCheckBox.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(original_formatCheckBox, gbc);
        last_updateCheckBox = new JCheckBox();
        last_updateCheckBox.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel18.add(last_updateCheckBox, gbc);
        final JScrollPane scrollPane12 = new JScrollPane();
        scrollPane12.setMaximumSize(new Dimension(550, 225));
        scrollPane12.setMinimumSize(new Dimension(550, 225));
        scrollPane12.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel17.add(scrollPane12, gbc);
        getContactsPublicPhotosOutput = new JTextArea();
        getContactsPublicPhotosOutput.setBackground(new Color(-3355444));
        scrollPane12.setViewportView(getContactsPublicPhotosOutput);
        getContactsPublicPhotosInvoke = new JButton();
        getContactsPublicPhotosInvoke.setFont(new Font(
                getContactsPublicPhotosInvoke.getFont().getName(), Font.BOLD,
                getContactsPublicPhotosInvoke.getFont().getSize()));
        getContactsPublicPhotosInvoke.setMaximumSize(new Dimension(100, 30));
        getContactsPublicPhotosInvoke.setMinimumSize(new Dimension(100, 30));
        getContactsPublicPhotosInvoke.setPreferredSize(new Dimension(100, 30));
        getContactsPublicPhotosInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel17.add(getContactsPublicPhotosInvoke, gbc);
        chkGetContactsPublicPhotosFriends = new JCheckBox();
        chkGetContactsPublicPhotosFriends.setHorizontalAlignment(4);
        chkGetContactsPublicPhotosFriends.setMaximumSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosFriends.setMinimumSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosFriends.setPreferredSize(new Dimension(300, 25));
        chkGetContactsPublicPhotosFriends.setText("Just Friends ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel17.add(chkGetContactsPublicPhotosFriends, gbc);
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetContext", panel19);
        final JLabel label29 = new JLabel();
        label29.setFont(
                new Font(label29.getFont().getName(), Font.BOLD, label29.getFont().getSize()));
        label29.setHorizontalAlignment(0);
        label29.setMaximumSize(new Dimension(600, 50));
        label29.setMinimumSize(new Dimension(600, 50));
        label29.setPreferredSize(new Dimension(600, 50));
        label29.setText("Returns next and previous photos for a photo in a photostream.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel19.add(label29, gbc);
        final JLabel label30 = new JLabel();
        label30.setFont(
                new Font(label30.getFont().getName(), Font.BOLD, label30.getFont().getSize()));
        label30.setHorizontalAlignment(4);
        label30.setMaximumSize(new Dimension(400, 25));
        label30.setMinimumSize(new Dimension(400, 25));
        label30.setPreferredSize(new Dimension(400, 25));
        label30.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel19.add(label30, gbc);
        txtGetContextPhotoID = new JTextField();
        txtGetContextPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGetContextPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGetContextPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel19.add(txtGetContextPhotoID, gbc);
        getContextInvoke = new JButton();
        getContextInvoke.setFont(new Font(getContextInvoke.getFont().getName(), Font.BOLD,
                                          getContextInvoke.getFont().getSize()));
        getContextInvoke.setMaximumSize(new Dimension(100, 30));
        getContextInvoke.setMinimumSize(new Dimension(100, 30));
        getContextInvoke.setPreferredSize(new Dimension(100, 30));
        getContextInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel19.add(getContextInvoke, gbc);
        final JScrollPane scrollPane13 = new JScrollPane();
        scrollPane13.setMaximumSize(new Dimension(550, 225));
        scrollPane13.setMinimumSize(new Dimension(550, 225));
        scrollPane13.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel19.add(scrollPane13, gbc);
        getContextOutput = new JTextArea();
        getContextOutput.setBackground(new Color(-3355444));
        scrollPane13.setViewportView(getContextOutput);
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridBagLayout());
        panel20.setMaximumSize(new Dimension(200, 25));
        panel20.setMinimumSize(new Dimension(200, 25));
        panel20.setPreferredSize(new Dimension(200, 25));
        photosOperationPane.addTab("GetCounts", panel20);
        final JLabel label31 = new JLabel();
        label31.setFont(
                new Font(label31.getFont().getName(), Font.BOLD, label31.getFont().getSize()));
        label31.setHorizontalAlignment(0);
        label31.setMaximumSize(new Dimension(600, 50));
        label31.setMinimumSize(new Dimension(600, 50));
        label31.setPreferredSize(new Dimension(600, 50));
        label31.setText(
                "Gets a list of photo counts for the given date ranges for the calling user.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel20.add(label31, gbc);
        final JLabel label32 = new JLabel();
        label32.setHorizontalAlignment(4);
        label32.setMaximumSize(new Dimension(400, 25));
        label32.setMinimumSize(new Dimension(400, 25));
        label32.setPreferredSize(new Dimension(400, 25));
        label32.setText("Dates           : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel20.add(label32, gbc);
        txtGetCountsDates = new JTextField();
        txtGetCountsDates.setMaximumSize(new Dimension(200, 25));
        txtGetCountsDates.setMinimumSize(new Dimension(200, 25));
        txtGetCountsDates.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel20.add(txtGetCountsDates, gbc);
        final JLabel label33 = new JLabel();
        label33.setHorizontalAlignment(4);
        label33.setMaximumSize(new Dimension(400, 25));
        label33.setMinimumSize(new Dimension(400, 25));
        label33.setPreferredSize(new Dimension(400, 25));
        label33.setText("Dates Taken : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel20.add(label33, gbc);
        txtGetCountsDatesTaken = new JTextField();
        txtGetCountsDatesTaken.setMaximumSize(new Dimension(200, 25));
        txtGetCountsDatesTaken.setMinimumSize(new Dimension(200, 25));
        txtGetCountsDatesTaken.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel20.add(txtGetCountsDatesTaken, gbc);
        getCountsInvoke = new JButton();
        getCountsInvoke.setFont(new Font(getCountsInvoke.getFont().getName(), Font.BOLD,
                                         getCountsInvoke.getFont().getSize()));
        getCountsInvoke.setLabel("Invoke");
        getCountsInvoke.setMaximumSize(new Dimension(100, 30));
        getCountsInvoke.setMinimumSize(new Dimension(100, 30));
        getCountsInvoke.setPreferredSize(new Dimension(100, 30));
        getCountsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel20.add(getCountsInvoke, gbc);
        final JScrollPane scrollPane14 = new JScrollPane();
        scrollPane14.setMaximumSize(new Dimension(550, 225));
        scrollPane14.setMinimumSize(new Dimension(550, 225));
        scrollPane14.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel20.add(scrollPane14, gbc);
        getCountsOutput = new JTextArea();
        getCountsOutput.setBackground(new Color(-3355444));
        scrollPane14.setViewportView(getCountsOutput);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetExif", panel21);
        final JLabel label34 = new JLabel();
        label34.setFont(
                new Font(label34.getFont().getName(), Font.BOLD, label34.getFont().getSize()));
        label34.setHorizontalAlignment(4);
        label34.setMaximumSize(new Dimension(400, 25));
        label34.setMinimumSize(new Dimension(400, 25));
        label34.setPreferredSize(new Dimension(400, 25));
        label34.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel21.add(label34, gbc);
        txtGetExifPhotoID = new JTextField();
        txtGetExifPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGetExifPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGetExifPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel21.add(txtGetExifPhotoID, gbc);
        final JLabel label35 = new JLabel();
        label35.setHorizontalAlignment(4);
        label35.setMaximumSize(new Dimension(400, 25));
        label35.setMinimumSize(new Dimension(400, 25));
        label35.setPreferredSize(new Dimension(400, 25));
        label35.setText("Secret      :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel21.add(label35, gbc);
        txtGetExifSecret = new JTextField();
        txtGetExifSecret.setMaximumSize(new Dimension(200, 25));
        txtGetExifSecret.setMinimumSize(new Dimension(200, 25));
        txtGetExifSecret.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel21.add(txtGetExifSecret, gbc);
        getExifInvoke = new JButton();
        getExifInvoke.setFont(new Font(getExifInvoke.getFont().getName(), Font.BOLD,
                                       getExifInvoke.getFont().getSize()));
        getExifInvoke.setLabel("Invoke");
        getExifInvoke.setMaximumSize(new Dimension(100, 30));
        getExifInvoke.setMinimumSize(new Dimension(100, 30));
        getExifInvoke.setPreferredSize(new Dimension(100, 30));
        getExifInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel21.add(getExifInvoke, gbc);
        final JLabel label36 = new JLabel();
        label36.setFont(
                new Font(label36.getFont().getName(), Font.BOLD, label36.getFont().getSize()));
        label36.setHorizontalAlignment(0);
        label36.setMaximumSize(new Dimension(600, 50));
        label36.setMinimumSize(new Dimension(600, 50));
        label36.setPreferredSize(new Dimension(600, 50));
        label36.setText("Retrieves a list of EXIF/TIFF/GPS tags for a given photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel21.add(label36, gbc);
        final JScrollPane scrollPane15 = new JScrollPane();
        scrollPane15.setMaximumSize(new Dimension(550, 225));
        scrollPane15.setMinimumSize(new Dimension(550, 225));
        scrollPane15.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel21.add(scrollPane15, gbc);
        getExifOutput = new JTextArea();
        getExifOutput.setBackground(new Color(-3355444));
        scrollPane15.setViewportView(getExifOutput);
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetFavorites ", panel22);
        final JLabel label37 = new JLabel();
        label37.setFont(
                new Font(label37.getFont().getName(), Font.BOLD, label37.getFont().getSize()));
        label37.setHorizontalAlignment(0);
        label37.setMaximumSize(new Dimension(600, 50));
        label37.setMinimumSize(new Dimension(600, 50));
        label37.setPreferredSize(new Dimension(600, 50));
        label37.setText("Returns the list of people who have favorited a given photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel22.add(label37, gbc);
        final JLabel label38 = new JLabel();
        label38.setFont(
                new Font(label38.getFont().getName(), Font.BOLD, label38.getFont().getSize()));
        label38.setHorizontalAlignment(4);
        label38.setMaximumSize(new Dimension(400, 25));
        label38.setMinimumSize(new Dimension(400, 25));
        label38.setPreferredSize(new Dimension(400, 25));
        label38.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel22.add(label38, gbc);
        txtGetFavoritesPhotoID = new JTextField();
        txtGetFavoritesPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGetFavoritesPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGetFavoritesPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel22.add(txtGetFavoritesPhotoID, gbc);
        final JLabel label39 = new JLabel();
        label39.setHorizontalAlignment(4);
        label39.setMaximumSize(new Dimension(400, 25));
        label39.setMinimumSize(new Dimension(400, 25));
        label39.setPreferredSize(new Dimension(400, 25));
        label39.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel22.add(label39, gbc);
        txtGetFavoritesPage = new JTextField();
        txtGetFavoritesPage.setMaximumSize(new Dimension(200, 25));
        txtGetFavoritesPage.setMinimumSize(new Dimension(200, 25));
        txtGetFavoritesPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel22.add(txtGetFavoritesPage, gbc);
        final JLabel label40 = new JLabel();
        label40.setHorizontalAlignment(4);
        label40.setMaximumSize(new Dimension(400, 25));
        label40.setMinimumSize(new Dimension(400, 25));
        label40.setPreferredSize(new Dimension(400, 25));
        label40.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel22.add(label40, gbc);
        cmbGetFavoritesPerPage = new JComboBox();
        cmbGetFavoritesPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGetFavoritesPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGetFavoritesPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel22.add(cmbGetFavoritesPerPage, gbc);
        getFavoritesInvoke = new JButton();
        getFavoritesInvoke.setFont(new Font(getFavoritesInvoke.getFont().getName(), Font.BOLD,
                                            getFavoritesInvoke.getFont().getSize()));
        getFavoritesInvoke.setMaximumSize(new Dimension(100, 30));
        getFavoritesInvoke.setMinimumSize(new Dimension(100, 30));
        getFavoritesInvoke.setPreferredSize(new Dimension(100, 30));
        getFavoritesInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel22.add(getFavoritesInvoke, gbc);
        final JScrollPane scrollPane16 = new JScrollPane();
        scrollPane16.setMaximumSize(new Dimension(550, 225));
        scrollPane16.setMinimumSize(new Dimension(550, 225));
        scrollPane16.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel22.add(scrollPane16, gbc);
        getFavoritesOutput = new JTextArea();
        getFavoritesOutput.setBackground(new Color(-3355444));
        scrollPane16.setViewportView(getFavoritesOutput);
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetNotInSet", panel23);
        final JLabel label41 = new JLabel();
        label41.setFont(
                new Font(label41.getFont().getName(), Font.BOLD, label41.getFont().getSize()));
        label41.setHorizontalAlignment(0);
        label41.setMaximumSize(new Dimension(600, 50));
        label41.setMinimumSize(new Dimension(600, 50));
        label41.setPreferredSize(new Dimension(600, 50));
        label41.setText("Returns a list of your photos that are not part of any sets.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        panel23.add(label41, gbc);
        final JLabel label42 = new JLabel();
        label42.setHorizontalAlignment(4);
        label42.setMaximumSize(new Dimension(200, 25));
        label42.setMinimumSize(new Dimension(200, 25));
        label42.setPreferredSize(new Dimension(200, 25));
        label42.setText("Minumum Upload Date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label42, gbc);
        txtGetNotInSetMinUpDate = new JTextField();
        txtGetNotInSetMinUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetNotInSetMinUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetNotInSetMinUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(txtGetNotInSetMinUpDate, gbc);
        final JLabel label43 = new JLabel();
        label43.setHorizontalAlignment(4);
        label43.setMaximumSize(new Dimension(200, 25));
        label43.setMinimumSize(new Dimension(200, 25));
        label43.setPreferredSize(new Dimension(200, 25));
        label43.setText("Maximum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label43, gbc);
        txtGetNotInSetMaxUpDate = new JTextField();
        txtGetNotInSetMaxUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetNotInSetMaxUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetNotInSetMaxUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(txtGetNotInSetMaxUpDate, gbc);
        final JLabel label44 = new JLabel();
        label44.setHorizontalAlignment(4);
        label44.setMaximumSize(new Dimension(200, 25));
        label44.setMinimumSize(new Dimension(200, 25));
        label44.setPreferredSize(new Dimension(200, 25));
        label44.setText("Minimum taken date     : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label44, gbc);
        txtGetNotInSetMinTakDate = new JTextField();
        txtGetNotInSetMinTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetNotInSetMinTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetNotInSetMinTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(txtGetNotInSetMinTakDate, gbc);
        txtGetNotInSetMaxTakDate = new JTextField();
        txtGetNotInSetMaxTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetNotInSetMaxTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetNotInSetMaxTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(txtGetNotInSetMaxTakDate, gbc);
        final JLabel label45 = new JLabel();
        label45.setHorizontalAlignment(4);
        label45.setMaximumSize(new Dimension(200, 25));
        label45.setMinimumSize(new Dimension(200, 25));
        label45.setPreferredSize(new Dimension(200, 25));
        label45.setText("Maximum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label45, gbc);
        final JLabel label46 = new JLabel();
        label46.setHorizontalAlignment(4);
        label46.setMaximumSize(new Dimension(200, 25));
        label46.setMinimumSize(new Dimension(200, 25));
        label46.setPreferredSize(new Dimension(200, 25));
        label46.setText("Page                           : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label46, gbc);
        txtGetNotInSetPage = new JTextField();
        txtGetNotInSetPage.setMaximumSize(new Dimension(200, 25));
        txtGetNotInSetPage.setMinimumSize(new Dimension(200, 25));
        txtGetNotInSetPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(txtGetNotInSetPage, gbc);
        final JLabel label47 = new JLabel();
        label47.setHorizontalAlignment(4);
        label47.setMaximumSize(new Dimension(200, 25));
        label47.setMinimumSize(new Dimension(200, 25));
        label47.setPreferredSize(new Dimension(200, 25));
        label47.setText("Privacy Filter                : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label47, gbc);
        cmbGetNotInSetPrivacy = new JComboBox();
        cmbGetNotInSetPrivacy.setMaximumSize(new Dimension(200, 25));
        cmbGetNotInSetPrivacy.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("public photos");
        defaultComboBoxModel1.addElement("private photos visible to friends");
        defaultComboBoxModel1.addElement("private photos visible to family");
        defaultComboBoxModel1.addElement("private photos visible to friends & family");
        defaultComboBoxModel1.addElement("completely private photos");
        cmbGetNotInSetPrivacy.setModel(defaultComboBoxModel1);
        cmbGetNotInSetPrivacy.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(cmbGetNotInSetPrivacy, gbc);
        getNotInSetInvoke = new JButton();
        getNotInSetInvoke.setFont(new Font(getNotInSetInvoke.getFont().getName(), Font.BOLD,
                                           getNotInSetInvoke.getFont().getSize()));
        getNotInSetInvoke.setMaximumSize(new Dimension(100, 30));
        getNotInSetInvoke.setMinimumSize(new Dimension(100, 30));
        getNotInSetInvoke.setPreferredSize(new Dimension(100, 30));
        getNotInSetInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel23.add(getNotInSetInvoke, gbc);
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel23.add(panel24, gbc);
        final JLabel label48 = new JLabel();
        label48.setFont(
                new Font(label48.getFont().getName(), Font.BOLD, label48.getFont().getSize()));
        label48.setHorizontalAlignment(0);
        label48.setMaximumSize(new Dimension(400, 25));
        label48.setMinimumSize(new Dimension(400, 25));
        label48.setPreferredSize(new Dimension(400, 25));
        label48.setText("Extra information to fetch for each returned record.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(label48, gbc);
        chkGetNotInSetLicense = new JCheckBox();
        chkGetNotInSetLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetLicense, gbc);
        chkGetNotInSetServer = new JCheckBox();
        chkGetNotInSetServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetServer, gbc);
        chkGetNotInSetOwner = new JCheckBox();
        chkGetNotInSetOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetOwner, gbc);
        chkGetNotInSetDateTak = new JCheckBox();
        chkGetNotInSetDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetDateTak, gbc);
        chkGetNotInSetDateUp = new JCheckBox();
        chkGetNotInSetDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetDateUp, gbc);
        chkGetNotInSetOriginal = new JCheckBox();
        chkGetNotInSetOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetOriginal, gbc);
        chkGetNotInSetLastUp = new JCheckBox();
        chkGetNotInSetLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetLastUp, gbc);
        chkGetNotInSetGeo = new JCheckBox();
        chkGetNotInSetGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetGeo, gbc);
        chkGetNotInSetTags = new JCheckBox();
        chkGetNotInSetTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetTags, gbc);
        chkGetNotInSetMachine = new JCheckBox();
        chkGetNotInSetMachine.setText("machine_tags.");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel24.add(chkGetNotInSetMachine, gbc);
        final JScrollPane scrollPane17 = new JScrollPane();
        scrollPane17.setMaximumSize(new Dimension(550, 225));
        scrollPane17.setMinimumSize(new Dimension(550, 225));
        scrollPane17.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel23.add(scrollPane17, gbc);
        getNotInSetOutput = new JTextArea();
        getNotInSetOutput.setBackground(new Color(-3355444));
        scrollPane17.setViewportView(getNotInSetOutput);
        final JLabel label49 = new JLabel();
        label49.setHorizontalAlignment(4);
        label49.setMaximumSize(new Dimension(200, 25));
        label49.setMinimumSize(new Dimension(200, 25));
        label49.setPreferredSize(new Dimension(200, 25));
        label49.setText("Records Per Page        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel23.add(label49, gbc);
        cmbGetNotInSetPerPage = new JComboBox();
        cmbGetNotInSetPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGetNotInSetPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGetNotInSetPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel23.add(cmbGetNotInSetPerPage, gbc);
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetPerms", panel25);
        final JLabel label50 = new JLabel();
        label50.setFont(
                new Font(label50.getFont().getName(), Font.BOLD, label50.getFont().getSize()));
        label50.setHorizontalAlignment(0);
        label50.setMaximumSize(new Dimension(600, 50));
        label50.setMinimumSize(new Dimension(600, 50));
        label50.setPreferredSize(new Dimension(600, 50));
        label50.setText("Get permissions for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel25.add(label50, gbc);
        final JLabel label51 = new JLabel();
        label51.setFont(
                new Font(label51.getFont().getName(), Font.BOLD, label51.getFont().getSize()));
        label51.setHorizontalAlignment(4);
        label51.setMaximumSize(new Dimension(400, 25));
        label51.setMinimumSize(new Dimension(400, 25));
        label51.setPreferredSize(new Dimension(400, 25));
        label51.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel25.add(label51, gbc);
        txtGetPermsPhotoID = new JTextField();
        txtGetPermsPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGetPermsPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGetPermsPhotoID.setOpaque(true);
        txtGetPermsPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel25.add(txtGetPermsPhotoID, gbc);
        getPermsInvoke = new JButton();
        getPermsInvoke.setFont(new Font(getPermsInvoke.getFont().getName(), Font.BOLD,
                                        getPermsInvoke.getFont().getSize()));
        getPermsInvoke.setMaximumSize(new Dimension(100, 30));
        getPermsInvoke.setMinimumSize(new Dimension(100, 30));
        getPermsInvoke.setPreferredSize(new Dimension(100, 30));
        getPermsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel25.add(getPermsInvoke, gbc);
        final JScrollPane scrollPane18 = new JScrollPane();
        scrollPane18.setMaximumSize(new Dimension(550, 225));
        scrollPane18.setMinimumSize(new Dimension(550, 225));
        scrollPane18.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel25.add(scrollPane18, gbc);
        getPermsOutput = new JTextArea();
        getPermsOutput.setBackground(new Color(-3355444));
        scrollPane18.setViewportView(getPermsOutput);
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetRecent ", panel26);
        final JLabel label52 = new JLabel();
        label52.setFont(
                new Font(label52.getFont().getName(), Font.BOLD, label52.getFont().getSize()));
        label52.setHorizontalAlignment(0);
        label52.setMaximumSize(new Dimension(600, 50));
        label52.setMinimumSize(new Dimension(600, 50));
        label52.setPreferredSize(new Dimension(600, 50));
        label52.setText("Returns a list of the latest public photos uploaded to flickr.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel26.add(label52, gbc);
        txtGetRecentPage = new JTextField();
        txtGetRecentPage.setMaximumSize(new Dimension(200, 25));
        txtGetRecentPage.setMinimumSize(new Dimension(200, 25));
        txtGetRecentPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel26.add(txtGetRecentPage, gbc);
        final JLabel label53 = new JLabel();
        label53.setHorizontalAlignment(4);
        label53.setMaximumSize(new Dimension(400, 25));
        label53.setMinimumSize(new Dimension(400, 25));
        label53.setPreferredSize(new Dimension(400, 25));
        label53.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel26.add(label53, gbc);
        cmbGetRecentPerPage = new JComboBox();
        cmbGetRecentPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGetRecentPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGetRecentPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel26.add(cmbGetRecentPerPage, gbc);
        getRecentInvoke = new JButton();
        getRecentInvoke.setFont(new Font(getRecentInvoke.getFont().getName(), Font.BOLD,
                                         getRecentInvoke.getFont().getSize()));
        getRecentInvoke.setMaximumSize(new Dimension(100, 30));
        getRecentInvoke.setMinimumSize(new Dimension(100, 30));
        getRecentInvoke.setPreferredSize(new Dimension(100, 30));
        getRecentInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 0, 0);
        panel26.add(getRecentInvoke, gbc);
        final JPanel panel27 = new JPanel();
        panel27.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel26.add(panel27, gbc);
        final JLabel label54 = new JLabel();
        label54.setFont(
                new Font(label54.getFont().getName(), Font.BOLD, label54.getFont().getSize()));
        label54.setMaximumSize(new Dimension(400, 25));
        label54.setMinimumSize(new Dimension(400, 25));
        label54.setPreferredSize(new Dimension(400, 25));
        label54.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(label54, gbc);
        chkGetRecentLicense = new JCheckBox();
        chkGetRecentLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentLicense, gbc);
        chkGetRecentDateUp = new JCheckBox();
        chkGetRecentDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentDateUp, gbc);
        chkGetRecentDateTak = new JCheckBox();
        chkGetRecentDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentDateTak, gbc);
        chkGetRecentOwner = new JCheckBox();
        chkGetRecentOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentOwner, gbc);
        chkGetRecentServer = new JCheckBox();
        chkGetRecentServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentServer, gbc);
        chkGetRecentOriginal = new JCheckBox();
        chkGetRecentOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentOriginal, gbc);
        chkGetRecentLastUp = new JCheckBox();
        chkGetRecentLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentLastUp, gbc);
        chkGetRecentGeo = new JCheckBox();
        chkGetRecentGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentGeo, gbc);
        chkGetRecentTags = new JCheckBox();
        chkGetRecentTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentTags, gbc);
        chkGetRecentMachine = new JCheckBox();
        chkGetRecentMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel27.add(chkGetRecentMachine, gbc);
        final JScrollPane scrollPane19 = new JScrollPane();
        scrollPane19.setMaximumSize(new Dimension(550, 225));
        scrollPane19.setMinimumSize(new Dimension(550, 225));
        scrollPane19.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel26.add(scrollPane19, gbc);
        getRecentOutput = new JTextArea();
        getRecentOutput.setBackground(new Color(-3355444));
        scrollPane19.setViewportView(getRecentOutput);
        final JLabel label55 = new JLabel();
        label55.setHorizontalAlignment(4);
        label55.setMaximumSize(new Dimension(400, 25));
        label55.setMinimumSize(new Dimension(400, 25));
        label55.setPreferredSize(new Dimension(400, 25));
        label55.setText("Page       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel26.add(label55, gbc);
        final JPanel panel28 = new JPanel();
        panel28.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetSizes", panel28);
        final JLabel label56 = new JLabel();
        label56.setFont(
                new Font(label56.getFont().getName(), Font.BOLD, label56.getFont().getSize()));
        label56.setHorizontalAlignment(0);
        label56.setMaximumSize(new Dimension(600, 50));
        label56.setMinimumSize(new Dimension(600, 50));
        label56.setPreferredSize(new Dimension(600, 50));
        label56.setText(
                "Returns the available sizes for a photo. The calling user must have permission to view the photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel28.add(label56, gbc);
        final JLabel label57 = new JLabel();
        label57.setFont(
                new Font(label57.getFont().getName(), Font.BOLD, label57.getFont().getSize()));
        label57.setHorizontalAlignment(4);
        label57.setMaximumSize(new Dimension(400, 25));
        label57.setMinimumSize(new Dimension(400, 25));
        label57.setPreferredSize(new Dimension(400, 25));
        label57.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel28.add(label57, gbc);
        txtGetSizesPhotoID = new JTextField();
        txtGetSizesPhotoID.setMaximumSize(new Dimension(200, 25));
        txtGetSizesPhotoID.setMinimumSize(new Dimension(200, 25));
        txtGetSizesPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel28.add(txtGetSizesPhotoID, gbc);
        getSizesInvoke = new JButton();
        getSizesInvoke.setFont(new Font(getSizesInvoke.getFont().getName(), Font.BOLD,
                                        getSizesInvoke.getFont().getSize()));
        getSizesInvoke.setLabel("Invoke");
        getSizesInvoke.setMaximumSize(new Dimension(100, 30));
        getSizesInvoke.setMinimumSize(new Dimension(100, 30));
        getSizesInvoke.setPreferredSize(new Dimension(100, 30));
        getSizesInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel28.add(getSizesInvoke, gbc);
        final JScrollPane scrollPane20 = new JScrollPane();
        scrollPane20.setMaximumSize(new Dimension(550, 225));
        scrollPane20.setMinimumSize(new Dimension(550, 225));
        scrollPane20.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel28.add(scrollPane20, gbc);
        getSizesOutput = new JTextArea();
        getSizesOutput.setBackground(new Color(-3355444));
        scrollPane20.setViewportView(getSizesOutput);
        final JPanel panel29 = new JPanel();
        panel29.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetUntagged", panel29);
        final JLabel label58 = new JLabel();
        label58.setFont(
                new Font(label58.getFont().getName(), Font.BOLD, label58.getFont().getSize()));
        label58.setHorizontalAlignment(0);
        label58.setMaximumSize(new Dimension(600, 50));
        label58.setMinimumSize(new Dimension(600, 50));
        label58.setPreferredSize(new Dimension(600, 50));
        label58.setText("Returns a list of your photos with no tags.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        panel29.add(label58, gbc);
        final JLabel label59 = new JLabel();
        label59.setHorizontalAlignment(4);
        label59.setMaximumSize(new Dimension(200, 25));
        label59.setMinimumSize(new Dimension(200, 25));
        label59.setPreferredSize(new Dimension(200, 25));
        label59.setText("Minimum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label59, gbc);
        txtGetUntaggedMinUpDate = new JTextField();
        txtGetUntaggedMinUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetUntaggedMinUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetUntaggedMinUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(txtGetUntaggedMinUpDate, gbc);
        final JLabel label60 = new JLabel();
        label60.setHorizontalAlignment(4);
        label60.setMaximumSize(new Dimension(200, 25));
        label60.setMinimumSize(new Dimension(200, 25));
        label60.setPreferredSize(new Dimension(200, 25));
        label60.setText("Minimum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label60, gbc);
        txtGetUntaggedMinTakDate = new JTextField();
        txtGetUntaggedMinTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetUntaggedMinTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetUntaggedMinTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(txtGetUntaggedMinTakDate, gbc);
        final JLabel label61 = new JLabel();
        label61.setHorizontalAlignment(4);
        label61.setMaximumSize(new Dimension(200, 25));
        label61.setMinimumSize(new Dimension(200, 25));
        label61.setPreferredSize(new Dimension(200, 25));
        label61.setText("Maximum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label61, gbc);
        txtGetUntaggedMaxUpDate = new JTextField();
        txtGetUntaggedMaxUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetUntaggedMaxUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetUntaggedMaxUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(txtGetUntaggedMaxUpDate, gbc);
        final JLabel label62 = new JLabel();
        label62.setHorizontalAlignment(4);
        label62.setMaximumSize(new Dimension(200, 25));
        label62.setMinimumSize(new Dimension(200, 25));
        label62.setPreferredSize(new Dimension(200, 25));
        label62.setText("Maximum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label62, gbc);
        txtGetUntaggedMaxTakDate = new JTextField();
        txtGetUntaggedMaxTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetUntaggedMaxTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetUntaggedMaxTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(txtGetUntaggedMaxTakDate, gbc);
        txtGetUntaggedPage = new JTextField();
        txtGetUntaggedPage.setMaximumSize(new Dimension(200, 25));
        txtGetUntaggedPage.setMinimumSize(new Dimension(200, 25));
        txtGetUntaggedPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(txtGetUntaggedPage, gbc);
        cmbGetUntaggedPerPage = new JComboBox();
        cmbGetUntaggedPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGetUntaggedPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGetUntaggedPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(cmbGetUntaggedPerPage, gbc);
        cmbGetUntaggedPrivacy = new JComboBox();
        cmbGetUntaggedPrivacy.setMaximumSize(new Dimension(200, 25));
        cmbGetUntaggedPrivacy.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("public photos");
        defaultComboBoxModel2.addElement("private photos visible to friends");
        defaultComboBoxModel2.addElement("private photos visible to family");
        defaultComboBoxModel2.addElement("private photos visible to friends & family");
        defaultComboBoxModel2.addElement("completely private photos");
        cmbGetUntaggedPrivacy.setModel(defaultComboBoxModel2);
        cmbGetUntaggedPrivacy.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel29.add(cmbGetUntaggedPrivacy, gbc);
        final JLabel label63 = new JLabel();
        label63.setHorizontalAlignment(4);
        label63.setMaximumSize(new Dimension(200, 25));
        label63.setMinimumSize(new Dimension(200, 25));
        label63.setPreferredSize(new Dimension(200, 25));
        label63.setText("Records Per Page        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label63, gbc);
        final JLabel label64 = new JLabel();
        label64.setHorizontalAlignment(4);
        label64.setMaximumSize(new Dimension(200, 25));
        label64.setMinimumSize(new Dimension(200, 25));
        label64.setPreferredSize(new Dimension(200, 25));
        label64.setText("Page                          : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label64, gbc);
        final JLabel label65 = new JLabel();
        label65.setHorizontalAlignment(4);
        label65.setMaximumSize(new Dimension(200, 25));
        label65.setMinimumSize(new Dimension(200, 25));
        label65.setPreferredSize(new Dimension(200, 25));
        label65.setText("Privacy Filter               : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel29.add(label65, gbc);
        getUntaggedInvoke = new JButton();
        getUntaggedInvoke.setFont(new Font(getUntaggedInvoke.getFont().getName(), Font.BOLD,
                                           getUntaggedInvoke.getFont().getSize()));
        getUntaggedInvoke.setLabel("Invoke");
        getUntaggedInvoke.setMaximumSize(new Dimension(100, 30));
        getUntaggedInvoke.setMinimumSize(new Dimension(100, 30));
        getUntaggedInvoke.setPreferredSize(new Dimension(100, 30));
        getUntaggedInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel29.add(getUntaggedInvoke, gbc);
        final JPanel panel30 = new JPanel();
        panel30.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel29.add(panel30, gbc);
        final JLabel label66 = new JLabel();
        label66.setFont(
                new Font(label66.getFont().getName(), Font.BOLD, label66.getFont().getSize()));
        label66.setHorizontalAlignment(0);
        label66.setMaximumSize(new Dimension(400, 25));
        label66.setMinimumSize(new Dimension(400, 25));
        label66.setPreferredSize(new Dimension(400, 25));
        label66.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(label66, gbc);
        chkGetUntaggedLicense = new JCheckBox();
        chkGetUntaggedLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedLicense, gbc);
        chkGetUntaggedDateUp = new JCheckBox();
        chkGetUntaggedDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedDateUp, gbc);
        chkGetUntaggedDateTak = new JCheckBox();
        chkGetUntaggedDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedDateTak, gbc);
        chkGetUntaggedOwner = new JCheckBox();
        chkGetUntaggedOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedOwner, gbc);
        chkGetUntaggedServer = new JCheckBox();
        chkGetUntaggedServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedServer, gbc);
        chkGetUntaggedOriginal = new JCheckBox();
        chkGetUntaggedOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedOriginal, gbc);
        chkGetUntaggedLastUp = new JCheckBox();
        chkGetUntaggedLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedLastUp, gbc);
        chkGetUntaggedGeo = new JCheckBox();
        chkGetUntaggedGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedGeo, gbc);
        chkGetUntaggedTags = new JCheckBox();
        chkGetUntaggedTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedTags, gbc);
        chkGetUntaggedMachine = new JCheckBox();
        chkGetUntaggedMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel30.add(chkGetUntaggedMachine, gbc);
        final JScrollPane scrollPane21 = new JScrollPane();
        scrollPane21.setMaximumSize(new Dimension(550, 225));
        scrollPane21.setMinimumSize(new Dimension(550, 225));
        scrollPane21.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel29.add(scrollPane21, gbc);
        getUntaggedOutput = new JTextArea();
        getUntaggedOutput.setBackground(new Color(-3355444));
        scrollPane21.setViewportView(getUntaggedOutput);
        final JPanel panel31 = new JPanel();
        panel31.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetWithGeoData", panel31);
        final JLabel label67 = new JLabel();
        label67.setFont(
                new Font(label67.getFont().getName(), Font.BOLD, label67.getFont().getSize()));
        label67.setHorizontalAlignment(0);
        label67.setMaximumSize(new Dimension(600, 50));
        label67.setMinimumSize(new Dimension(600, 50));
        label67.setPreferredSize(new Dimension(600, 50));
        label67.setText("Returns a list of your geo-tagged photos.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        panel31.add(label67, gbc);
        final JLabel label68 = new JLabel();
        label68.setHorizontalAlignment(4);
        label68.setMaximumSize(new Dimension(200, 25));
        label68.setMinimumSize(new Dimension(200, 25));
        label68.setPreferredSize(new Dimension(200, 25));
        label68.setText("Minimum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label68, gbc);
        txtGetWithGeoDataMinUpDate = new JTextField();
        txtGetWithGeoDataMinUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithGeoDataMinUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithGeoDataMinUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(txtGetWithGeoDataMinUpDate, gbc);
        final JLabel label69 = new JLabel();
        label69.setHorizontalAlignment(4);
        label69.setMaximumSize(new Dimension(200, 25));
        label69.setMinimumSize(new Dimension(200, 25));
        label69.setPreferredSize(new Dimension(200, 25));
        label69.setText("Maximum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label69, gbc);
        txtGetWithGeoDataMaxUpDate = new JTextField();
        txtGetWithGeoDataMaxUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithGeoDataMaxUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithGeoDataMaxUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(txtGetWithGeoDataMaxUpDate, gbc);
        final JLabel label70 = new JLabel();
        label70.setHorizontalAlignment(4);
        label70.setMaximumSize(new Dimension(200, 25));
        label70.setMinimumSize(new Dimension(200, 25));
        label70.setPreferredSize(new Dimension(200, 25));
        label70.setText("Maximum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label70, gbc);
        final JLabel label71 = new JLabel();
        label71.setHorizontalAlignment(4);
        label71.setMaximumSize(new Dimension(200, 25));
        label71.setMinimumSize(new Dimension(200, 25));
        label71.setPreferredSize(new Dimension(200, 25));
        label71.setText("Minimum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label71, gbc);
        final JLabel label72 = new JLabel();
        label72.setHorizontalAlignment(4);
        label72.setMaximumSize(new Dimension(200, 25));
        label72.setMinimumSize(new Dimension(200, 25));
        label72.setPreferredSize(new Dimension(200, 25));
        label72.setText("Records Per Page        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label72, gbc);
        final JLabel label73 = new JLabel();
        label73.setHorizontalAlignment(4);
        label73.setMaximumSize(new Dimension(200, 25));
        label73.setMinimumSize(new Dimension(200, 25));
        label73.setPreferredSize(new Dimension(200, 25));
        label73.setText("Page                          : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label73, gbc);
        final JLabel label74 = new JLabel();
        label74.setHorizontalAlignment(4);
        label74.setMaximumSize(new Dimension(200, 25));
        label74.setMinimumSize(new Dimension(200, 25));
        label74.setPreferredSize(new Dimension(200, 25));
        label74.setText("Privacy Filter               : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel31.add(label74, gbc);
        txtGetWithGeoDataMaxTakDate = new JTextField();
        txtGetWithGeoDataMaxTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithGeoDataMaxTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithGeoDataMaxTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(txtGetWithGeoDataMaxTakDate, gbc);
        txtGetWithGeoDataMinTakDate = new JTextField();
        txtGetWithGeoDataMinTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithGeoDataMinTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithGeoDataMinTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(txtGetWithGeoDataMinTakDate, gbc);
        txtGetWithGeoDataPage = new JTextField();
        txtGetWithGeoDataPage.setMaximumSize(new Dimension(200, 25));
        txtGetWithGeoDataPage.setMinimumSize(new Dimension(200, 25));
        txtGetWithGeoDataPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(txtGetWithGeoDataPage, gbc);
        cmbGetWithGeoDataPerPage = new JComboBox();
        cmbGetWithGeoDataPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGetWithGeoDataPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGetWithGeoDataPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(cmbGetWithGeoDataPerPage, gbc);
        cmbGetWithGeoDataPrivacy = new JComboBox();
        cmbGetWithGeoDataPrivacy.setMaximumSize(new Dimension(200, 25));
        cmbGetWithGeoDataPrivacy.setMinimumSize(new Dimension(200, 25));
        cmbGetWithGeoDataPrivacy.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel31.add(cmbGetWithGeoDataPrivacy, gbc);
        getWithGeoDataInvoke = new JButton();
        getWithGeoDataInvoke.setFont(new Font(getWithGeoDataInvoke.getFont().getName(), Font.BOLD,
                                              getWithGeoDataInvoke.getFont().getSize()));
        getWithGeoDataInvoke.setMaximumSize(new Dimension(100, 30));
        getWithGeoDataInvoke.setMinimumSize(new Dimension(100, 30));
        getWithGeoDataInvoke.setPreferredSize(new Dimension(100, 30));
        getWithGeoDataInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel31.add(getWithGeoDataInvoke, gbc);
        final JPanel panel32 = new JPanel();
        panel32.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel31.add(panel32, gbc);
        final JLabel label75 = new JLabel();
        label75.setFont(
                new Font(label75.getFont().getName(), Font.BOLD, label75.getFont().getSize()));
        label75.setHorizontalAlignment(0);
        label75.setMaximumSize(new Dimension(200, 25));
        label75.setMinimumSize(new Dimension(200, 25));
        label75.setPreferredSize(new Dimension(200, 25));
        label75.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(label75, gbc);
        chkGetWithGeoDataLicense = new JCheckBox();
        chkGetWithGeoDataLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataLicense, gbc);
        chkGetWithGeoDataDateUp = new JCheckBox();
        chkGetWithGeoDataDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataDateUp, gbc);
        chkGetWithGeoDataDateTak = new JCheckBox();
        chkGetWithGeoDataDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataDateTak, gbc);
        chkGetWithGeoDataOwner = new JCheckBox();
        chkGetWithGeoDataOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataOwner, gbc);
        chkGetWithGeoDataServer = new JCheckBox();
        chkGetWithGeoDataServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataServer, gbc);
        chkGetWithGeoDataOriginal = new JCheckBox();
        chkGetWithGeoDataOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataOriginal, gbc);
        chkGetWithGeoDataLastUp = new JCheckBox();
        chkGetWithGeoDataLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataLastUp, gbc);
        chkGetWithGeoDataGeo = new JCheckBox();
        chkGetWithGeoDataGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataGeo, gbc);
        chkGetWithGeoDataTags = new JCheckBox();
        chkGetWithGeoDataTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataTags, gbc);
        chkGetWithGeoDataMachine = new JCheckBox();
        chkGetWithGeoDataMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel32.add(chkGetWithGeoDataMachine, gbc);
        final JScrollPane scrollPane22 = new JScrollPane();
        scrollPane22.setMaximumSize(new Dimension(550, 225));
        scrollPane22.setMinimumSize(new Dimension(550, 225));
        scrollPane22.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel31.add(scrollPane22, gbc);
        getWithGeoDataOutput = new JTextArea();
        getWithGeoDataOutput.setBackground(new Color(-3355444));
        scrollPane22.setViewportView(getWithGeoDataOutput);
        final JPanel panel33 = new JPanel();
        panel33.setLayout(new GridBagLayout());
        photosOperationPane.addTab("GetWithoutGeoData", panel33);
        final JLabel label76 = new JLabel();
        label76.setFont(
                new Font(label76.getFont().getName(), Font.BOLD, label76.getFont().getSize()));
        label76.setHorizontalAlignment(0);
        label76.setMaximumSize(new Dimension(600, 50));
        label76.setMinimumSize(new Dimension(600, 50));
        label76.setPreferredSize(new Dimension(600, 50));
        label76.setText("Returns a list of your photos which haven't been geo-tagged.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        panel33.add(label76, gbc);
        final JLabel label77 = new JLabel();
        label77.setHorizontalAlignment(4);
        label77.setMaximumSize(new Dimension(200, 25));
        label77.setMinimumSize(new Dimension(200, 25));
        label77.setPreferredSize(new Dimension(200, 25));
        label77.setText("Minimum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label77, gbc);
        txtGetWithoutGeoDataMinUpDate = new JTextField();
        txtGetWithoutGeoDataMinUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMinUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMinUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(txtGetWithoutGeoDataMinUpDate, gbc);
        final JLabel label78 = new JLabel();
        label78.setHorizontalAlignment(4);
        label78.setMaximumSize(new Dimension(200, 25));
        label78.setMinimumSize(new Dimension(200, 25));
        label78.setPreferredSize(new Dimension(200, 25));
        label78.setText("Maximum upload date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label78, gbc);
        txtGetWithoutGeoDataMaxUpDate = new JTextField();
        txtGetWithoutGeoDataMaxUpDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMaxUpDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMaxUpDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(txtGetWithoutGeoDataMaxUpDate, gbc);
        final JLabel label79 = new JLabel();
        label79.setHorizontalAlignment(4);
        label79.setMaximumSize(new Dimension(200, 25));
        label79.setMinimumSize(new Dimension(200, 25));
        label79.setPreferredSize(new Dimension(200, 25));
        label79.setText("Maximum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label79, gbc);
        final JLabel label80 = new JLabel();
        label80.setHorizontalAlignment(4);
        label80.setMaximumSize(new Dimension(200, 25));
        label80.setMinimumSize(new Dimension(200, 25));
        label80.setPreferredSize(new Dimension(200, 25));
        label80.setText("Minimum taken date   : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label80, gbc);
        final JLabel label81 = new JLabel();
        label81.setHorizontalAlignment(4);
        label81.setMaximumSize(new Dimension(200, 25));
        label81.setMinimumSize(new Dimension(200, 25));
        label81.setPreferredSize(new Dimension(200, 25));
        label81.setText("Records Per Page        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label81, gbc);
        final JLabel label82 = new JLabel();
        label82.setHorizontalAlignment(4);
        label82.setMaximumSize(new Dimension(200, 25));
        label82.setMinimumSize(new Dimension(200, 25));
        label82.setPreferredSize(new Dimension(200, 25));
        label82.setText("Page                          : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label82, gbc);
        final JLabel label83 = new JLabel();
        label83.setHorizontalAlignment(4);
        label83.setMaximumSize(new Dimension(200, 25));
        label83.setMinimumSize(new Dimension(200, 25));
        label83.setPreferredSize(new Dimension(200, 25));
        label83.setText("Privacy Filter               : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel33.add(label83, gbc);
        txtGetWithoutGeoDataMaxTakDate = new JPasswordField();
        txtGetWithoutGeoDataMaxTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMaxTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMaxTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(txtGetWithoutGeoDataMaxTakDate, gbc);
        txtGetWithoutGeoDataMinTakDate = new JTextField();
        txtGetWithoutGeoDataMinTakDate.setMaximumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMinTakDate.setMinimumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataMinTakDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(txtGetWithoutGeoDataMinTakDate, gbc);
        txtGetWithoutGeoDataPage = new JTextField();
        txtGetWithoutGeoDataPage.setMaximumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataPage.setMinimumSize(new Dimension(200, 25));
        txtGetWithoutGeoDataPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(txtGetWithoutGeoDataPage, gbc);
        cmbGetWithoutGeoDataPerPage = new JComboBox();
        cmbGetWithoutGeoDataPerPage.setMaximumSize(new Dimension(200, 25));
        cmbGetWithoutGeoDataPerPage.setMinimumSize(new Dimension(200, 25));
        cmbGetWithoutGeoDataPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(cmbGetWithoutGeoDataPerPage, gbc);
        cmbGetWithoutGeoDataPrivacy = new JComboBox();
        cmbGetWithoutGeoDataPrivacy.setMaximumSize(new Dimension(200, 25));
        cmbGetWithoutGeoDataPrivacy.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel3 = new DefaultComboBoxModel();
        defaultComboBoxModel3.addElement("public photos");
        defaultComboBoxModel3.addElement("private photos visible to friends");
        defaultComboBoxModel3.addElement("private photos visible to family");
        defaultComboBoxModel3.addElement("private photos visible to friends & family");
        defaultComboBoxModel3.addElement("completely private photos");
        cmbGetWithoutGeoDataPrivacy.setModel(defaultComboBoxModel3);
        cmbGetWithoutGeoDataPrivacy.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel33.add(cmbGetWithoutGeoDataPrivacy, gbc);
        getWithoutGeoDataInvoke = new JButton();
        getWithoutGeoDataInvoke.setFont(new Font(getWithoutGeoDataInvoke.getFont().getName(),
                                                 Font.BOLD,
                                                 getWithoutGeoDataInvoke.getFont().getSize()));
        getWithoutGeoDataInvoke.setLabel("Invoke");
        getWithoutGeoDataInvoke.setMaximumSize(new Dimension(100, 30));
        getWithoutGeoDataInvoke.setMinimumSize(new Dimension(100, 30));
        getWithoutGeoDataInvoke.setPreferredSize(new Dimension(100, 30));
        getWithoutGeoDataInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel33.add(getWithoutGeoDataInvoke, gbc);
        final JPanel panel34 = new JPanel();
        panel34.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel33.add(panel34, gbc);
        final JLabel label84 = new JLabel();
        label84.setFont(
                new Font(label84.getFont().getName(), Font.BOLD, label84.getFont().getSize()));
        label84.setHorizontalAlignment(0);
        label84.setMaximumSize(new Dimension(600, 25));
        label84.setMinimumSize(new Dimension(600, 25));
        label84.setPreferredSize(new Dimension(600, 25));
        label84.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(label84, gbc);
        chkGetWithoutGeoDataLicense = new JCheckBox();
        chkGetWithoutGeoDataLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataLicense, gbc);
        chkGetWithoutGeoDataDateUp = new JCheckBox();
        chkGetWithoutGeoDataDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataDateUp, gbc);
        chkGetWithoutGeoDataDateTak = new JCheckBox();
        chkGetWithoutGeoDataDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataDateTak, gbc);
        chkGetWithoutGeoDataGeo = new JCheckBox();
        chkGetWithoutGeoDataGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataGeo, gbc);
        chkGetWithoutGeoDataOwner = new JCheckBox();
        chkGetWithoutGeoDataOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataOwner, gbc);
        chkGetWithoutGeoDataTags = new JCheckBox();
        chkGetWithoutGeoDataTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataTags, gbc);
        chkGetWithoutGeoDataLastUp = new JCheckBox();
        chkGetWithoutGeoDataLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataLastUp, gbc);
        chkGetWithoutGeoDataOriginal = new JCheckBox();
        chkGetWithoutGeoDataOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel34.add(chkGetWithoutGeoDataOriginal, gbc);
        chkGetWithoutGeoDataServer = new JCheckBox();
        chkGetWithoutGeoDataServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel34.add(chkGetWithoutGeoDataServer, gbc);
        chkGetWithoutGeoDataMachine = new JCheckBox();
        chkGetWithoutGeoDataMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel34.add(chkGetWithoutGeoDataMachine, gbc);
        final JScrollPane scrollPane23 = new JScrollPane();
        scrollPane23.setMaximumSize(new Dimension(550, 225));
        scrollPane23.setMinimumSize(new Dimension(550, 225));
        scrollPane23.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel33.add(scrollPane23, gbc);
        getWithoutGeoDataOutput = new JTextArea();
        getWithoutGeoDataOutput.setBackground(new Color(-3355444));
        scrollPane23.setViewportView(getWithoutGeoDataOutput);
        final JPanel panel35 = new JPanel();
        panel35.setLayout(new GridBagLayout());
        photosOperationPane.addTab("RecentlyUpdated ", panel35);
        final JLabel label85 = new JLabel();
        label85.setFont(
                new Font(label85.getFont().getName(), Font.BOLD, label85.getFont().getSize()));
        label85.setHorizontalAlignment(0);
        label85.setMaximumSize(new Dimension(616, 50));
        label85.setMinimumSize(new Dimension(616, 50));
        label85.setPreferredSize(new Dimension(616, 50));
        label85.setText(
                "Return a list of your photos that have been recently created or which have been recently modified.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel35.add(label85, gbc);
        final JLabel label86 = new JLabel();
        label86.setHorizontalAlignment(4);
        label86.setMaximumSize(new Dimension(400, 25));
        label86.setMinimumSize(new Dimension(400, 25));
        label86.setPreferredSize(new Dimension(400, 25));
        label86.setText("Minimum Date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel35.add(label86, gbc);
        txtRecentlyUpdatedMinDate = new JTextField();
        txtRecentlyUpdatedMinDate.setMaximumSize(new Dimension(200, 25));
        txtRecentlyUpdatedMinDate.setMinimumSize(new Dimension(200, 25));
        txtRecentlyUpdatedMinDate.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel35.add(txtRecentlyUpdatedMinDate, gbc);
        final JLabel label87 = new JLabel();
        label87.setHorizontalAlignment(4);
        label87.setMaximumSize(new Dimension(400, 25));
        label87.setMinimumSize(new Dimension(400, 25));
        label87.setPreferredSize(new Dimension(400, 25));
        label87.setText("Page               : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel35.add(label87, gbc);
        txtRecentlyUpdatedPage = new JTextField();
        txtRecentlyUpdatedPage.setMaximumSize(new Dimension(200, 25));
        txtRecentlyUpdatedPage.setMinimumSize(new Dimension(200, 25));
        txtRecentlyUpdatedPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel35.add(txtRecentlyUpdatedPage, gbc);
        final JLabel label88 = new JLabel();
        label88.setHorizontalAlignment(4);
        label88.setMaximumSize(new Dimension(400, 25));
        label88.setMinimumSize(new Dimension(400, 25));
        label88.setPreferredSize(new Dimension(400, 25));
        label88.setText("Per Page          :");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel35.add(label88, gbc);
        recentlyUpdatedInvoke = new JButton();
        recentlyUpdatedInvoke.setFont(new Font(recentlyUpdatedInvoke.getFont().getName(), Font.BOLD,
                                               recentlyUpdatedInvoke.getFont().getSize()));
        recentlyUpdatedInvoke.setLabel("Invoke");
        recentlyUpdatedInvoke.setMaximumSize(new Dimension(100, 30));
        recentlyUpdatedInvoke.setMinimumSize(new Dimension(100, 30));
        recentlyUpdatedInvoke.setPreferredSize(new Dimension(100, 30));
        recentlyUpdatedInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 0, 0);
        panel35.add(recentlyUpdatedInvoke, gbc);
        final JPanel panel36 = new JPanel();
        panel36.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel35.add(panel36, gbc);
        final JLabel label89 = new JLabel();
        label89.setFont(
                new Font(label89.getFont().getName(), Font.BOLD, label89.getFont().getSize()));
        label89.setHorizontalAlignment(0);
        label89.setMaximumSize(new Dimension(319, 25));
        label89.setMinimumSize(new Dimension(319, 25));
        label89.setPreferredSize(new Dimension(319, 25));
        label89.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(label89, gbc);
        chkRecentlyUpdatedLicense = new JCheckBox();
        chkRecentlyUpdatedLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedLicense, gbc);
        chkRecentlyUpdatedServer = new JCheckBox();
        chkRecentlyUpdatedServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedServer, gbc);
        chkRecentlyUpdatedOwner = new JCheckBox();
        chkRecentlyUpdatedOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedOwner, gbc);
        chkRecentlyUpdatedDateTak = new JCheckBox();
        chkRecentlyUpdatedDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedDateTak, gbc);
        chkRecentlyUpdatedDateUp = new JCheckBox();
        chkRecentlyUpdatedDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedDateUp, gbc);
        chkRecentlyUpdatedOriginal = new JCheckBox();
        chkRecentlyUpdatedOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedOriginal, gbc);
        chkRecentlyUpdatedLastUp = new JCheckBox();
        chkRecentlyUpdatedLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedLastUp, gbc);
        chkRecentlyUpdatedGeo = new JCheckBox();
        chkRecentlyUpdatedGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedGeo, gbc);
        chkRecentlyUpdatedTags = new JCheckBox();
        chkRecentlyUpdatedTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedTags, gbc);
        chkRecentlyUpdatedMachine = new JCheckBox();
        chkRecentlyUpdatedMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel36.add(chkRecentlyUpdatedMachine, gbc);
        final JScrollPane scrollPane24 = new JScrollPane();
        scrollPane24.setMaximumSize(new Dimension(550, 225));
        scrollPane24.setMinimumSize(new Dimension(550, 225));
        scrollPane24.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel35.add(scrollPane24, gbc);
        recentlyUpdatedOutput = new JTextArea();
        recentlyUpdatedOutput.setBackground(new Color(-3355444));
        scrollPane24.setViewportView(recentlyUpdatedOutput);
        cmbRecentlyUpdatedPerPage = new JComboBox();
        cmbRecentlyUpdatedPerPage.setMaximumSize(new Dimension(200, 25));
        cmbRecentlyUpdatedPerPage.setMinimumSize(new Dimension(200, 25));
        cmbRecentlyUpdatedPerPage.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 10, 0);
        panel35.add(cmbRecentlyUpdatedPerPage, gbc);
        final JPanel panel37 = new JPanel();
        panel37.setLayout(new GridBagLayout());
        photosOperationPane.addTab("RemoveTag", panel37);
        final JLabel label90 = new JLabel();
        label90.setFont(
                new Font(label90.getFont().getName(), Font.BOLD, label90.getFont().getSize()));
        label90.setHorizontalAlignment(0);
        label90.setMaximumSize(new Dimension(177, 50));
        label90.setMinimumSize(new Dimension(177, 50));
        label90.setPreferredSize(new Dimension(177, 50));
        label90.setText("Remove a tag from a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel37.add(label90, gbc);
        final JLabel label91 = new JLabel();
        label91.setFont(
                new Font(label91.getFont().getName(), Font.BOLD, label91.getFont().getSize()));
        label91.setHorizontalAlignment(4);
        label91.setMaximumSize(new Dimension(400, 25));
        label91.setMinimumSize(new Dimension(400, 25));
        label91.setPreferredSize(new Dimension(400, 25));
        label91.setText("Tag ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel37.add(label91, gbc);
        txtRemoveTagTadID = new JTextField();
        txtRemoveTagTadID.setMaximumSize(new Dimension(200, 25));
        txtRemoveTagTadID.setMinimumSize(new Dimension(200, 25));
        txtRemoveTagTadID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 30, 0, 0);
        panel37.add(txtRemoveTagTadID, gbc);
        removeTagInvoke = new JButton();
        removeTagInvoke.setFont(new Font(removeTagInvoke.getFont().getName(), Font.BOLD,
                                         removeTagInvoke.getFont().getSize()));
        removeTagInvoke.setLabel("Invoke");
        removeTagInvoke.setMaximumSize(new Dimension(100, 30));
        removeTagInvoke.setMinimumSize(new Dimension(100, 30));
        removeTagInvoke.setPreferredSize(new Dimension(100, 30));
        removeTagInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel37.add(removeTagInvoke, gbc);
        final JScrollPane scrollPane25 = new JScrollPane();
        scrollPane25.setMaximumSize(new Dimension(550, 225));
        scrollPane25.setMinimumSize(new Dimension(550, 225));
        scrollPane25.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel37.add(scrollPane25, gbc);
        removeTagOutput = new JTextArea();
        removeTagOutput.setBackground(new Color(-3355444));
        scrollPane25.setViewportView(removeTagOutput);
        final JPanel panel38 = new JPanel();
        panel38.setLayout(new GridBagLayout());
        photosOperationPane.addTab("Search", panel38);
        final JLabel label92 = new JLabel();
        label92.setFont(
                new Font(label92.getFont().getName(), Font.BOLD, label92.getFont().getSize()));
        label92.setHorizontalAlignment(0);
        label92.setMaximumSize(new Dimension(600, 50));
        label92.setMinimumSize(new Dimension(600, 50));
        label92.setPreferredSize(new Dimension(600, 50));
        label92.setText("Return a list of photos matching some criteria. ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.NORTH;
        panel38.add(label92, gbc);
        final JLabel label93 = new JLabel();
        label93.setFont(new Font(label93.getFont().getName(), label93.getFont().getStyle(),
                                 label93.getFont().getSize()));
        label93.setHorizontalAlignment(4);
        label93.setMaximumSize(new Dimension(130, 25));
        label93.setMinimumSize(new Dimension(130, 25));
        label93.setPreferredSize(new Dimension(130, 25));
        label93.setText("User ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label93, gbc);
        txtSearchUserID = new JTextField();
        txtSearchUserID.setMaximumSize(new Dimension(130, 25));
        txtSearchUserID.setMinimumSize(new Dimension(130, 25));
        txtSearchUserID.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchUserID, gbc);
        final JLabel label94 = new JLabel();
        label94.setHorizontalAlignment(4);
        label94.setMaximumSize(new Dimension(130, 25));
        label94.setMinimumSize(new Dimension(130, 25));
        label94.setPreferredSize(new Dimension(130, 25));
        label94.setText("Text : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label94, gbc);
        txtSearchText = new JTextField();
        txtSearchText.setMaximumSize(new Dimension(130, 25));
        txtSearchText.setMinimumSize(new Dimension(130, 25));
        txtSearchText.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchText, gbc);
        final JLabel label95 = new JLabel();
        label95.setHorizontalAlignment(4);
        label95.setMaximumSize(new Dimension(130, 25));
        label95.setMinimumSize(new Dimension(130, 25));
        label95.setPreferredSize(new Dimension(130, 25));
        label95.setText("Tags : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label95, gbc);
        txtSearchTags = new JTextField();
        txtSearchTags.setMaximumSize(new Dimension(130, 25));
        txtSearchTags.setMinimumSize(new Dimension(130, 25));
        txtSearchTags.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchTags, gbc);
        final JLabel label96 = new JLabel();
        label96.setHorizontalAlignment(4);
        label96.setMaximumSize(new Dimension(130, 25));
        label96.setMinimumSize(new Dimension(130, 25));
        label96.setPreferredSize(new Dimension(130, 25));
        label96.setText("Tag Mode : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label96, gbc);
        cmbSearchTagMode = new JComboBox();
        cmbSearchTagMode.setMaximumSize(new Dimension(130, 25));
        cmbSearchTagMode.setMinimumSize(new Dimension(130, 25));
        final DefaultComboBoxModel defaultComboBoxModel4 = new DefaultComboBoxModel();
        defaultComboBoxModel4.addElement("None");
        defaultComboBoxModel4.addElement("Any");
        defaultComboBoxModel4.addElement("Or");
        cmbSearchTagMode.setModel(defaultComboBoxModel4);
        cmbSearchTagMode.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(cmbSearchTagMode, gbc);
        final JLabel label97 = new JLabel();
        label97.setHorizontalAlignment(4);
        label97.setMaximumSize(new Dimension(130, 25));
        label97.setMinimumSize(new Dimension(130, 25));
        label97.setPreferredSize(new Dimension(130, 25));
        label97.setText("Min Upload Date: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label97, gbc);
        txtSearchMinUpDate = new JTextField();
        txtSearchMinUpDate.setMaximumSize(new Dimension(130, 25));
        txtSearchMinUpDate.setMinimumSize(new Dimension(130, 25));
        txtSearchMinUpDate.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchMinUpDate, gbc);
        final JLabel label98 = new JLabel();
        label98.setHorizontalAlignment(4);
        label98.setMaximumSize(new Dimension(130, 25));
        label98.setMinimumSize(new Dimension(130, 25));
        label98.setPreferredSize(new Dimension(130, 25));
        label98.setText("Max Upload Date: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label98, gbc);
        txtSearchMaxUpDate = new JTextField();
        txtSearchMaxUpDate.setMaximumSize(new Dimension(130, 25));
        txtSearchMaxUpDate.setMinimumSize(new Dimension(130, 25));
        txtSearchMaxUpDate.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchMaxUpDate, gbc);
        final JLabel label99 = new JLabel();
        label99.setHorizontalAlignment(4);
        label99.setMaximumSize(new Dimension(130, 25));
        label99.setMinimumSize(new Dimension(130, 25));
        label99.setPreferredSize(new Dimension(130, 25));
        label99.setText("Max Taken Date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label99, gbc);
        txtMaxTakDate = new JTextField();
        txtMaxTakDate.setMaximumSize(new Dimension(130, 25));
        txtMaxTakDate.setMinimumSize(new Dimension(130, 25));
        txtMaxTakDate.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtMaxTakDate, gbc);
        final JLabel label100 = new JLabel();
        label100.setHorizontalAlignment(4);
        label100.setMaximumSize(new Dimension(130, 25));
        label100.setMinimumSize(new Dimension(130, 25));
        label100.setPreferredSize(new Dimension(130, 25));
        label100.setText("Group ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label100, gbc);
        txtSearchGroupID = new JTextField();
        txtSearchGroupID.setMaximumSize(new Dimension(130, 25));
        txtSearchGroupID.setMinimumSize(new Dimension(130, 25));
        txtSearchGroupID.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchGroupID, gbc);
        final JLabel label101 = new JLabel();
        label101.setHorizontalAlignment(4);
        label101.setMaximumSize(new Dimension(130, 25));
        label101.setMinimumSize(new Dimension(130, 25));
        label101.setPreferredSize(new Dimension(130, 25));
        label101.setText("Machine Tag Mode : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label101, gbc);
        cmbSearchMachineMode = new JComboBox();
        cmbSearchMachineMode.setMaximumSize(new Dimension(130, 25));
        cmbSearchMachineMode.setMinimumSize(new Dimension(130, 25));
        final DefaultComboBoxModel defaultComboBoxModel5 = new DefaultComboBoxModel();
        defaultComboBoxModel5.addElement("Any");
        defaultComboBoxModel5.addElement("Or");
        cmbSearchMachineMode.setModel(defaultComboBoxModel5);
        cmbSearchMachineMode.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(cmbSearchMachineMode, gbc);
        searchInvoke = new JButton();
        searchInvoke.setFont(new Font(searchInvoke.getFont().getName(), Font.BOLD,
                                      searchInvoke.getFont().getSize()));
        searchInvoke.setLabel("Invoke");
        searchInvoke.setMaximumSize(new Dimension(100, 30));
        searchInvoke.setMinimumSize(new Dimension(100, 30));
        searchInvoke.setPreferredSize(new Dimension(100, 30));
        searchInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 6;
        panel38.add(searchInvoke, gbc);
        final JPanel panel39 = new JPanel();
        panel39.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.BOTH;
        panel38.add(panel39, gbc);
        final JLabel label102 = new JLabel();
        label102.setFont(
                new Font(label102.getFont().getName(), Font.BOLD, label102.getFont().getSize()));
        label102.setHorizontalAlignment(0);
        label102.setMaximumSize(new Dimension(319, 25));
        label102.setMinimumSize(new Dimension(319, 25));
        label102.setPreferredSize(new Dimension(319, 25));
        label102.setText("Extra information to fetch for each returned record");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(label102, gbc);
        chkSearchLicense = new JCheckBox();
        chkSearchLicense.setText("license");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchLicense, gbc);
        chkSearchServer = new JCheckBox();
        chkSearchServer.setText("icon_server");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchServer, gbc);
        chkSearchOwner = new JCheckBox();
        chkSearchOwner.setText("owner_name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchOwner, gbc);
        chkSearchDateTak = new JCheckBox();
        chkSearchDateTak.setText("date_taken");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchDateTak, gbc);
        chkSearchDateUp = new JCheckBox();
        chkSearchDateUp.setText("date_upload");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchDateUp, gbc);
        chkSearchOriginal = new JCheckBox();
        chkSearchOriginal.setText("original_format");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchOriginal, gbc);
        chkSearchLastUp = new JCheckBox();
        chkSearchLastUp.setText("last_update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchLastUp, gbc);
        chkSearchGeo = new JCheckBox();
        chkSearchGeo.setText("geo");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchGeo, gbc);
        chkSearchTags = new JCheckBox();
        chkSearchTags.setText("tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchTags, gbc);
        chkSearchMachine = new JCheckBox();
        chkSearchMachine.setText("machine_tags");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel39.add(chkSearchMachine, gbc);
        final JScrollPane scrollPane26 = new JScrollPane();
        scrollPane26.setMaximumSize(new Dimension(550, 225));
        scrollPane26.setMinimumSize(new Dimension(550, 225));
        scrollPane26.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 6;
        gbc.weighty = 1.0;
        panel38.add(scrollPane26, gbc);
        searchOutput = new JTextArea();
        searchOutput.setBackground(new Color(-3355444));
        scrollPane26.setViewportView(searchOutput);
        final JLabel label103 = new JLabel();
        label103.setHorizontalAlignment(4);
        label103.setMaximumSize(new Dimension(130, 25));
        label103.setMinimumSize(new Dimension(130, 25));
        label103.setPreferredSize(new Dimension(130, 25));
        label103.setText("License : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label103, gbc);
        txtSearchLicense = new JTextField();
        txtSearchLicense.setMaximumSize(new Dimension(130, 25));
        txtSearchLicense.setMinimumSize(new Dimension(130, 25));
        txtSearchLicense.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchLicense, gbc);
        cmbSearchSort = new JComboBox();
        cmbSearchSort.setMaximumSize(new Dimension(130, 25));
        cmbSearchSort.setMinimumSize(new Dimension(130, 25));
        final DefaultComboBoxModel defaultComboBoxModel6 = new DefaultComboBoxModel();
        defaultComboBoxModel6.addElement("None");
        defaultComboBoxModel6.addElement("date-posted-asc");
        defaultComboBoxModel6.addElement("date-posted-desc");
        defaultComboBoxModel6.addElement("date-taken-asc");
        defaultComboBoxModel6.addElement("date-taken-desc");
        defaultComboBoxModel6.addElement("interestingness-desc");
        defaultComboBoxModel6.addElement("interestingness-asc");
        defaultComboBoxModel6.addElement("relevance");
        cmbSearchSort.setModel(defaultComboBoxModel6);
        cmbSearchSort.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(cmbSearchSort, gbc);
        final JLabel label104 = new JLabel();
        label104.setHorizontalAlignment(4);
        label104.setMaximumSize(new Dimension(130, 25));
        label104.setMinimumSize(new Dimension(130, 25));
        label104.setPreferredSize(new Dimension(130, 25));
        label104.setText("Sort : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label104, gbc);
        final JLabel label105 = new JLabel();
        label105.setHorizontalAlignment(4);
        label105.setMaximumSize(new Dimension(130, 25));
        label105.setMinimumSize(new Dimension(130, 25));
        label105.setPreferredSize(new Dimension(130, 25));
        label105.setText("Min Taken Date : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label105, gbc);
        txtSearchMinTakDate = new JTextField();
        txtSearchMinTakDate.setMaximumSize(new Dimension(130, 25));
        txtSearchMinTakDate.setMinimumSize(new Dimension(130, 25));
        txtSearchMinTakDate.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchMinTakDate, gbc);
        final JLabel label106 = new JLabel();
        label106.setHorizontalAlignment(4);
        label106.setMaximumSize(new Dimension(130, 25));
        label106.setMinimumSize(new Dimension(130, 25));
        label106.setPreferredSize(new Dimension(130, 25));
        label106.setText("Mchine Tags : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label106, gbc);
        txtSearchMachine = new JTextField();
        txtSearchMachine.setMaximumSize(new Dimension(130, 25));
        txtSearchMachine.setMinimumSize(new Dimension(130, 25));
        txtSearchMachine.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchMachine, gbc);
        final JLabel label107 = new JLabel();
        label107.setHorizontalAlignment(4);
        label107.setMaximumSize(new Dimension(130, 25));
        label107.setMinimumSize(new Dimension(130, 25));
        label107.setPreferredSize(new Dimension(130, 25));
        label107.setText("Accuracy : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label107, gbc);
        cmbSearchAccuracy = new JComboBox();
        cmbSearchAccuracy.setMaximumSize(new Dimension(130, 25));
        cmbSearchAccuracy.setMinimumSize(new Dimension(130, 25));
        cmbSearchAccuracy.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(cmbSearchAccuracy, gbc);
        final JLabel label108 = new JLabel();
        label108.setHorizontalAlignment(4);
        label108.setMaximumSize(new Dimension(130, 25));
        label108.setMinimumSize(new Dimension(130, 25));
        label108.setPreferredSize(new Dimension(130, 25));
        label108.setText("Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label108, gbc);
        final JLabel label109 = new JLabel();
        label109.setHorizontalAlignment(4);
        label109.setMaximumSize(new Dimension(130, 25));
        label109.setMinimumSize(new Dimension(130, 25));
        label109.setPreferredSize(new Dimension(130, 25));
        label109.setText("Per Page : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label109, gbc);
        final JLabel label110 = new JLabel();
        label110.setHorizontalAlignment(4);
        label110.setMaximumSize(new Dimension(130, 25));
        label110.setMinimumSize(new Dimension(130, 25));
        label110.setPreferredSize(new Dimension(130, 25));
        label110.setText("Privacy Filter : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel38.add(label110, gbc);
        cmbSearchPerPage = new JComboBox();
        cmbSearchPerPage.setMaximumSize(new Dimension(130, 25));
        cmbSearchPerPage.setMinimumSize(new Dimension(130, 25));
        final DefaultComboBoxModel defaultComboBoxModel8 = new DefaultComboBoxModel();
        cmbSearchPerPage.setModel(defaultComboBoxModel8);
        cmbSearchPerPage.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(cmbSearchPerPage, gbc);
        txtSearchPage = new JTextField();
        txtSearchPage.setMaximumSize(new Dimension(130, 25));
        txtSearchPage.setMinimumSize(new Dimension(130, 25));
        txtSearchPage.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(txtSearchPage, gbc);
        cmbSearchPrivacy = new JComboBox();
        cmbSearchPrivacy.setMaximumSize(new Dimension(130, 25));
        cmbSearchPrivacy.setMinimumSize(new Dimension(130, 25));
        final DefaultComboBoxModel defaultComboBoxModel9 = new DefaultComboBoxModel();
        defaultComboBoxModel9.addElement("None");
        defaultComboBoxModel9.addElement("public photos");
        defaultComboBoxModel9.addElement("private photos visible to friends");
        defaultComboBoxModel9.addElement("private photos visible to family");
        defaultComboBoxModel9.addElement("private photos visible to friends & family");
        defaultComboBoxModel9.addElement("completely private photos");
        cmbSearchPrivacy.setModel(defaultComboBoxModel9);
        cmbSearchPrivacy.setPreferredSize(new Dimension(130, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 20, 5, 0);
        panel38.add(cmbSearchPrivacy, gbc);
        final JPanel panel40 = new JPanel();
        panel40.setLayout(new GridBagLayout());
        photosOperationPane.addTab("SetDates", panel40);
        final JLabel label111 = new JLabel();
        label111.setFont(
                new Font(label111.getFont().getName(), Font.BOLD, label111.getFont().getSize()));
        label111.setHorizontalAlignment(0);
        label111.setMaximumSize(new Dimension(600, 50));
        label111.setMinimumSize(new Dimension(600, 50));
        label111.setPreferredSize(new Dimension(600, 50));
        label111.setText("Set one or both of the dates for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel40.add(label111, gbc);
        final JLabel label112 = new JLabel();
        label112.setFont(
                new Font(label112.getFont().getName(), Font.BOLD, label112.getFont().getSize()));
        label112.setHorizontalAlignment(4);
        label112.setMaximumSize(new Dimension(400, 25));
        label112.setMinimumSize(new Dimension(400, 25));
        label112.setPreferredSize(new Dimension(400, 25));
        label112.setText("Photo ID                                : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel40.add(label112, gbc);
        txtSetDatesPhotoID = new JTextField();
        txtSetDatesPhotoID.setMaximumSize(new Dimension(200, 25));
        txtSetDatesPhotoID.setMinimumSize(new Dimension(200, 25));
        txtSetDatesPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel40.add(txtSetDatesPhotoID, gbc);
        final JLabel label113 = new JLabel();
        label113.setHorizontalAlignment(4);
        label113.setMaximumSize(new Dimension(400, 25));
        label113.setMinimumSize(new Dimension(400, 25));
        label113.setPreferredSize(new Dimension(400, 25));
        label113.setText("Date Posted                            : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel40.add(label113, gbc);
        txtSetDatesPosted = new JTextField();
        txtSetDatesPosted.setMaximumSize(new Dimension(200, 25));
        txtSetDatesPosted.setMinimumSize(new Dimension(200, 25));
        txtSetDatesPosted.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel40.add(txtSetDatesPosted, gbc);
        setDatesInvoke = new JButton();
        setDatesInvoke.setFont(new Font(setDatesInvoke.getFont().getName(), Font.BOLD,
                                        setDatesInvoke.getFont().getSize()));
        setDatesInvoke.setMaximumSize(new Dimension(100, 30));
        setDatesInvoke.setMinimumSize(new Dimension(100, 30));
        setDatesInvoke.setPreferredSize(new Dimension(100, 30));
        setDatesInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel40.add(setDatesInvoke, gbc);
        final JScrollPane scrollPane27 = new JScrollPane();
        scrollPane27.setMaximumSize(new Dimension(550, 225));
        scrollPane27.setMinimumSize(new Dimension(550, 225));
        scrollPane27.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel40.add(scrollPane27, gbc);
        setDatesOutput = new JTextArea();
        setDatesOutput.setBackground(new Color(-3355444));
        scrollPane27.setViewportView(setDatesOutput);
        txtSetDatesTaken = new JTextField();
        txtSetDatesTaken.setMaximumSize(new Dimension(200, 25));
        txtSetDatesTaken.setMinimumSize(new Dimension(200, 25));
        txtSetDatesTaken.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel40.add(txtSetDatesTaken, gbc);
        final JLabel label114 = new JLabel();
        label114.setHorizontalAlignment(4);
        label114.setMaximumSize(new Dimension(400, 25));
        label114.setMinimumSize(new Dimension(400, 25));
        label114.setPreferredSize(new Dimension(400, 25));
        label114.setText("Date Taken                            : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel40.add(label114, gbc);
        final JLabel label115 = new JLabel();
        label115.setHorizontalAlignment(4);
        label115.setMaximumSize(new Dimension(400, 25));
        label115.setMinimumSize(new Dimension(400, 25));
        label115.setPreferredSize(new Dimension(400, 25));
        label115.setText("Granularity of Date Taken        : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel40.add(label115, gbc);
        txtSetDatesGranularity = new JTextField();
        txtSetDatesGranularity.setMaximumSize(new Dimension(200, 25));
        txtSetDatesGranularity.setMinimumSize(new Dimension(200, 25));
        txtSetDatesGranularity.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel40.add(txtSetDatesGranularity, gbc);
        final JPanel panel41 = new JPanel();
        panel41.setLayout(new GridBagLayout());
        photosOperationPane.addTab("SetMeta", panel41);
        final JLabel label116 = new JLabel();
        label116.setFont(
                new Font(label116.getFont().getName(), Font.BOLD, label116.getFont().getSize()));
        label116.setHorizontalAlignment(0);
        label116.setMaximumSize(new Dimension(600, 50));
        label116.setMinimumSize(new Dimension(600, 50));
        label116.setPreferredSize(new Dimension(600, 50));
        label116.setText("Set the meta information for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel41.add(label116, gbc);
        final JLabel label117 = new JLabel();
        label117.setFont(
                new Font(label117.getFont().getName(), Font.BOLD, label117.getFont().getSize()));
        label117.setHorizontalAlignment(4);
        label117.setMaximumSize(new Dimension(400, 25));
        label117.setMinimumSize(new Dimension(400, 25));
        label117.setPreferredSize(new Dimension(400, 25));
        label117.setText("Photo ID    : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel41.add(label117, gbc);
        txtSetMetaPhotoID = new JTextField();
        txtSetMetaPhotoID.setMaximumSize(new Dimension(200, 25));
        txtSetMetaPhotoID.setMinimumSize(new Dimension(200, 25));
        txtSetMetaPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel41.add(txtSetMetaPhotoID, gbc);
        final JLabel label118 = new JLabel();
        label118.setHorizontalAlignment(4);
        label118.setMaximumSize(new Dimension(400, 25));
        label118.setMinimumSize(new Dimension(400, 25));
        label118.setPreferredSize(new Dimension(400, 25));
        label118.setText("Title           : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        panel41.add(label118, gbc);
        txtSetMetaTitle = new JTextField();
        txtSetMetaTitle.setMaximumSize(new Dimension(200, 25));
        txtSetMetaTitle.setMinimumSize(new Dimension(200, 25));
        txtSetMetaTitle.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel41.add(txtSetMetaTitle, gbc);
        final JLabel label119 = new JLabel();
        label119.setHorizontalAlignment(4);
        label119.setMaximumSize(new Dimension(400, 25));
        label119.setMinimumSize(new Dimension(400, 25));
        label119.setPreferredSize(new Dimension(400, 25));
        label119.setText("Description : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel41.add(label119, gbc);
        setMetaInvoke = new JButton();
        setMetaInvoke.setFont(new Font(setMetaInvoke.getFont().getName(), Font.BOLD,
                                       setMetaInvoke.getFont().getSize()));
        setMetaInvoke.setMaximumSize(new Dimension(100, 30));
        setMetaInvoke.setMinimumSize(new Dimension(100, 30));
        setMetaInvoke.setPreferredSize(new Dimension(100, 30));
        setMetaInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel41.add(setMetaInvoke, gbc);
        final JScrollPane scrollPane28 = new JScrollPane();
        scrollPane28.setMaximumSize(new Dimension(550, 225));
        scrollPane28.setMinimumSize(new Dimension(550, 225));
        scrollPane28.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel41.add(scrollPane28, gbc);
        setMetaOutput = new JTextArea();
        setMetaOutput.setBackground(new Color(-3355444));
        scrollPane28.setViewportView(setMetaOutput);
        txtSetMetaDescription = new JTextField();
        txtSetMetaDescription.setMaximumSize(new Dimension(200, 25));
        txtSetMetaDescription.setMinimumSize(new Dimension(200, 25));
        txtSetMetaDescription.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel41.add(txtSetMetaDescription, gbc);
        final JPanel panel42 = new JPanel();
        panel42.setLayout(new GridBagLayout());
        photosOperationPane.addTab("SetPerms ", panel42);
        final JLabel label120 = new JLabel();
        label120.setFont(
                new Font(label120.getFont().getName(), Font.BOLD, label120.getFont().getSize()));
        label120.setHorizontalAlignment(0);
        label120.setMaximumSize(new Dimension(600, 50));
        label120.setMinimumSize(new Dimension(600, 50));
        label120.setPreferredSize(new Dimension(600, 50));
        label120.setText("Set permissions for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(25, 0, 25, 0);
        panel42.add(label120, gbc);
        final JLabel label121 = new JLabel();
        label121.setFont(
                new Font(label121.getFont().getName(), Font.BOLD, label121.getFont().getSize()));
        label121.setHorizontalAlignment(4);
        label121.setMaximumSize(new Dimension(400, 25));
        label121.setMinimumSize(new Dimension(400, 25));
        label121.setPreferredSize(new Dimension(400, 25));
        label121.setText("Photo ID                               : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel42.add(label121, gbc);
        chkSetPermsFriends = new JCheckBox();
        chkSetPermsFriends.setFont(new Font(chkSetPermsFriends.getFont().getName(), Font.BOLD,
                                            chkSetPermsFriends.getFont().getSize()));
        chkSetPermsFriends.setHorizontalAlignment(4);
        chkSetPermsFriends.setMaximumSize(new Dimension(300, 25));
        chkSetPermsFriends.setMinimumSize(new Dimension(300, 25));
        chkSetPermsFriends.setPreferredSize(new Dimension(300, 25));
        chkSetPermsFriends.setText("Is Friend");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel42.add(chkSetPermsFriends, gbc);
        chkSetPermsPublic = new JCheckBox();
        chkSetPermsPublic.setFont(new Font(chkSetPermsPublic.getFont().getName(), Font.BOLD,
                                           chkSetPermsPublic.getFont().getSize()));
        chkSetPermsPublic.setMaximumSize(new Dimension(200, 25));
        chkSetPermsPublic.setMinimumSize(new Dimension(200, 25));
        chkSetPermsPublic.setPreferredSize(new Dimension(200, 25));
        chkSetPermsPublic.setText("Is Public");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel42.add(chkSetPermsPublic, gbc);
        final JScrollPane scrollPane29 = new JScrollPane();
        scrollPane29.setMaximumSize(new Dimension(550, 225));
        scrollPane29.setMinimumSize(new Dimension(550, 225));
        scrollPane29.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        panel42.add(scrollPane29, gbc);
        setPermsOutput = new JTextArea();
        setPermsOutput.setBackground(new Color(-3355444));
        scrollPane29.setViewportView(setPermsOutput);
        setPermsInvoke = new JButton();
        setPermsInvoke.setFont(new Font(setPermsInvoke.getFont().getName(), Font.BOLD,
                                        setPermsInvoke.getFont().getSize()));
        setPermsInvoke.setLabel("Invoke");
        setPermsInvoke.setMaximumSize(new Dimension(100, 30));
        setPermsInvoke.setMinimumSize(new Dimension(100, 30));
        setPermsInvoke.setPreferredSize(new Dimension(100, 30));
        setPermsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel42.add(setPermsInvoke, gbc);
        chkSetPermsFamily = new JCheckBox();
        chkSetPermsFamily.setFont(new Font(chkSetPermsFamily.getFont().getName(), Font.BOLD,
                                         chkSetPermsFamily.getFont().getSize()));
        chkSetPermsFamily.setHorizontalAlignment(4);
        chkSetPermsFamily.setLabel("Is Family ");
        chkSetPermsFamily.setMaximumSize(new Dimension(300, 25));
        chkSetPermsFamily.setMinimumSize(new Dimension(300, 25));
        chkSetPermsFamily.setPreferredSize(new Dimension(300, 25));
        chkSetPermsFamily.setText("Is Family ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel42.add(chkSetPermsFamily, gbc);
        txtSetPermsPhotoID = new JTextField();
        txtSetPermsPhotoID.setMaximumSize(new Dimension(200, 25));
        txtSetPermsPhotoID.setMinimumSize(new Dimension(200, 25));
        txtSetPermsPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 15, 0);
        panel42.add(txtSetPermsPhotoID, gbc);
        final JLabel label122 = new JLabel();
        label122.setFont(
                new Font(label122.getFont().getName(), Font.BOLD, label122.getFont().getSize()));
        label122.setHorizontalAlignment(4);
        label122.setMaximumSize(new Dimension(400, 25));
        label122.setMinimumSize(new Dimension(400, 25));
        label122.setPreferredSize(new Dimension(400, 25));
        label122.setText("Permision to add Comments : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel42.add(label122, gbc);
        final JLabel label123 = new JLabel();
        label123.setFont(
                new Font(label123.getFont().getName(), Font.BOLD, label123.getFont().getSize()));
        label123.setHorizontalAlignment(4);
        label123.setMaximumSize(new Dimension(400, 25));
        label123.setMinimumSize(new Dimension(400, 25));
        label123.setPreferredSize(new Dimension(400, 25));
        label123.setText("Permision to add Meta          : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel42.add(label123, gbc);
        cmbSetPermsComments = new JComboBox();
        cmbSetPermsComments.setMaximumSize(new Dimension(200, 25));
        cmbSetPermsComments.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel10 = new DefaultComboBoxModel();
        defaultComboBoxModel10.addElement("Nobody");
        defaultComboBoxModel10.addElement("Friends & family");
        defaultComboBoxModel10.addElement("Contacts");
        defaultComboBoxModel10.addElement("Everybody");
        cmbSetPermsComments.setModel(defaultComboBoxModel10);
        cmbSetPermsComments.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel42.add(cmbSetPermsComments, gbc);
        cmbSetPermsMeta = new JComboBox();
        cmbSetPermsMeta.setMaximumSize(new Dimension(200, 25));
        cmbSetPermsMeta.setMinimumSize(new Dimension(200, 25));
        final DefaultComboBoxModel defaultComboBoxModel11 = new DefaultComboBoxModel();
        defaultComboBoxModel11.addElement("Nobody / Just the owner");
        defaultComboBoxModel11.addElement("Friends & family");
        defaultComboBoxModel11.addElement("Contacts");
        defaultComboBoxModel11.addElement("Everybody");
        cmbSetPermsMeta.setModel(defaultComboBoxModel11);
        cmbSetPermsMeta.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 10, 0);
        panel42.add(cmbSetPermsMeta, gbc);
        final JPanel panel43 = new JPanel();
        panel43.setLayout(new GridBagLayout());
        photosOperationPane.addTab("SetTags", panel43);
        txtSetTagsPhotoID = new JTextField();
        txtSetTagsPhotoID.setMaximumSize(new Dimension(200, 25));
        txtSetTagsPhotoID.setMinimumSize(new Dimension(200, 25));
        txtSetTagsPhotoID.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 25, 0);
        panel43.add(txtSetTagsPhotoID, gbc);
        final JLabel label124 = new JLabel();
        label124.setFont(
                new Font(label124.getFont().getName(), Font.BOLD, label124.getFont().getSize()));
        label124.setHorizontalAlignment(4);
        label124.setMaximumSize(new Dimension(400, 25));
        label124.setMinimumSize(new Dimension(400, 25));
        label124.setPreferredSize(new Dimension(400, 25));
        label124.setText("Photo ID : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 25, 0);
        panel43.add(label124, gbc);
        txtSetTagsTags = new JTextField();
        txtSetTagsTags.setMaximumSize(new Dimension(200, 25));
        txtSetTagsTags.setMinimumSize(new Dimension(200, 25));
        txtSetTagsTags.setPreferredSize(new Dimension(200, 25));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 100, 0, 0);
        panel43.add(txtSetTagsTags, gbc);
        final JLabel label125 = new JLabel();
        label125.setHorizontalAlignment(4);
        label125.setMaximumSize(new Dimension(400, 25));
        label125.setMinimumSize(new Dimension(400, 25));
        label125.setPreferredSize(new Dimension(400, 25));
        label125.setText("Tags       : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel43.add(label125, gbc);
        SetTagsInvoke = new JButton();
        SetTagsInvoke.setFont(new Font(SetTagsInvoke.getFont().getName(), Font.BOLD,
                                       SetTagsInvoke.getFont().getSize()));
        SetTagsInvoke.setLabel("Invoke");
        SetTagsInvoke.setMaximumSize(new Dimension(100, 30));
        SetTagsInvoke.setMinimumSize(new Dimension(100, 30));
        SetTagsInvoke.setPreferredSize(new Dimension(100, 30));
        SetTagsInvoke.setText("Invoke");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(25, 0, 50, 0);
        panel43.add(SetTagsInvoke, gbc);
        final JScrollPane scrollPane30 = new JScrollPane();
        scrollPane30.setMaximumSize(new Dimension(550, 225));
        scrollPane30.setMinimumSize(new Dimension(550, 225));
        scrollPane30.setPreferredSize(new Dimension(550, 225));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel43.add(scrollPane30, gbc);
        setTagsOutput = new JTextArea();
        setTagsOutput.setEditable(false);
        scrollPane30.setViewportView(setTagsOutput);
        final JLabel label126 = new JLabel();
        label126.setFont(
                new Font(label126.getFont().getName(), Font.BOLD, label126.getFont().getSize()));
        label126.setHorizontalAlignment(0);
        label126.setMaximumSize(new Dimension(600, 50));
        label126.setMinimumSize(new Dimension(600, 50));
        label126.setPreferredSize(new Dimension(600, 50));
        label126.setText("Set the tags for a photo.");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 0, 50, 0);
        panel43.add(label126, gbc);

    }

    public JComponent getRootComponent() {
        return panel1;
    }
}