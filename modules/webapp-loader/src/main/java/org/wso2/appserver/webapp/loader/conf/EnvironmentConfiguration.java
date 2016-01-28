/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.appserver.webapp.loader.conf;

import org.wso2.appserver.webapp.loader.LoaderConstants;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * An instance of this class holds environment configuration information of the class loader.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "delegatedEnvironments",
        "exclusiveEnvironments"
}, namespace = LoaderConstants.XSD_NAMESPACE)
@XmlRootElement(name = "ClassloaderEnvironments", namespace = LoaderConstants.XSD_NAMESPACE)
public class EnvironmentConfiguration {

    @XmlElement(name = "DelegatedEnvironments", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
    protected EnvironmentConfiguration.DelegatedEnvironments delegatedEnvironments;
    @XmlElement(name = "ExclusiveEnvironments", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
    protected EnvironmentConfiguration.ExclusiveEnvironments exclusiveEnvironments;

    /**
     * Gets the value of the delegatedEnvironments property.
     *
     * @return possible object is
     * {@link EnvironmentConfiguration.DelegatedEnvironments }
     */
    public EnvironmentConfiguration.DelegatedEnvironments getDelegatedEnvironments() {
        return delegatedEnvironments;
    }

    /**
     * Sets the value of the delegatedEnvironments property.
     *
     * @param value allowed object is
     *              {@link EnvironmentConfiguration.DelegatedEnvironments }
     */
    public void setDelegatedEnvironments(EnvironmentConfiguration.DelegatedEnvironments value) {
        this.delegatedEnvironments = value;
    }

    /**
     * Gets the value of the exclusiveEnvironments property.
     *
     * @return possible object is
     * {@link EnvironmentConfiguration.ExclusiveEnvironments }
     */
    public EnvironmentConfiguration.ExclusiveEnvironments getExclusiveEnvironments() {
        return exclusiveEnvironments;
    }

    /**
     * Sets the value of the exclusiveEnvironments property.
     *
     * @param value allowed object is
     *              {@link EnvironmentConfiguration.ExclusiveEnvironments }
     */
    public void setExclusiveEnvironments(EnvironmentConfiguration.ExclusiveEnvironments value) {
        this.exclusiveEnvironments = value;
    }

    /**
     * Stores delegated environment details.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "delegatedEnvironment"
    }, namespace = LoaderConstants.XSD_NAMESPACE)
    public static class DelegatedEnvironments {

        @XmlElement(name = "DelegatedEnvironment", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
        protected List<EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment> delegatedEnvironment;

        /**
         * Gets the value of the delegatedEnvironment property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the delegatedEnvironment property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDelegatedEnvironment().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment }
         */
        public List<EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment> getDelegatedEnvironment() {
            if (delegatedEnvironment == null) {
                delegatedEnvironment = new ArrayList<>();
            }
            return this.delegatedEnvironment;
        }

        /**
         * store specific delegated environment data.
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "name",
                "delegatedPackages",
                "delegatedResources"
        }, namespace = LoaderConstants.XSD_NAMESPACE)
        public static class DelegatedEnvironment {

            @XmlElement(name = "Name", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
            protected String name;
            @XmlElement(name = "DelegatedPackages", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
            protected EnvironmentConfiguration.DelegatedEnvironments
                    .DelegatedEnvironment.DelegatedPackages delegatedPackages;
            @XmlElement(name = "DelegatedResources", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
            protected EnvironmentConfiguration.DelegatedEnvironments.
                    DelegatedEnvironment.DelegatedResources delegatedResources;

            /**
             * Gets the value of the name property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the delegatedPackages property.
             *
             * @return possible object is
             * {@link EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment.DelegatedPackages }
             */
            public EnvironmentConfiguration.DelegatedEnvironments
                    .DelegatedEnvironment.DelegatedPackages getDelegatedPackages() {
                return delegatedPackages;
            }

            /**
             * Sets the value of the delegatedPackages property.
             *
             * @param value allowed object is
             *              {@link EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment
             *              .DelegatedPackages }
             */
            public void setDelegatedPackages(EnvironmentConfiguration.DelegatedEnvironments.
                                                     DelegatedEnvironment.DelegatedPackages value) {
                this.delegatedPackages = value;
            }

            /**
             * Gets the value of the delegatedResources property.
             *
             * @return possible object is
             * {@link EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment.DelegatedResources }
             */
            public EnvironmentConfiguration.DelegatedEnvironments.DelegatedEnvironment
                    .DelegatedResources getDelegatedResources() {
                return delegatedResources;
            }

            /**
             * Sets the value of the delegatedResources property.
             *
             * @param value allowed object is
             *              {@link EnvironmentConfiguration.DelegatedEnvironments
             *              .DelegatedEnvironment.DelegatedResources }
             */
            public void setDelegatedResources(EnvironmentConfiguration.DelegatedEnvironments
                                                      .DelegatedEnvironment.DelegatedResources value) {
                this.delegatedResources = value;
            }

            /**
             * stores delegated package details.
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "delegatedPackage"
            }, namespace = LoaderConstants.XSD_NAMESPACE)
            public static class DelegatedPackages {

                @XmlElement(name = "DelegatedPackage", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
                protected List<String> delegatedPackage;

                /**
                 * Gets the value of the delegatedPackage property.
                 * <p>
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the delegatedPackage property.
                 * <p>
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getDelegatedPackage().add(newItem);
                 * </pre>
                 * <p>
                 * <p>
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 */
                public List<String> getDelegatedPackage() {
                    if (delegatedPackage == null) {
                        delegatedPackage = new ArrayList<String>();
                    }
                    return this.delegatedPackage;
                }

            }

            /**
             * stores delegated resource details.
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "delegatedResource"
            }, namespace = LoaderConstants.XSD_NAMESPACE)
            public static class DelegatedResources {

                @XmlElement(name = "DelegatedResource", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
                protected List<String> delegatedResource;

                /**
                 * Gets the value of the delegatedResource property.
                 * <p>
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the delegatedResource property.
                 * <p>
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getDelegatedResource().add(newItem);
                 * </pre>
                 * <p>
                 * <p>
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 */
                public List<String> getDelegatedResource() {
                    if (delegatedResource == null) {
                        delegatedResource = new ArrayList<String>();
                    }
                    return this.delegatedResource;
                }

            }

        }

    }

    /**
     * stores exclusive environment details.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "exclusiveEnvironment"
    }, namespace = LoaderConstants.XSD_NAMESPACE)
    public static class ExclusiveEnvironments {

        @XmlElement(name = "ExclusiveEnvironment", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
        protected List<EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment> exclusiveEnvironment;

        /**
         * Gets the value of the exclusiveEnvironment property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the exclusiveEnvironment property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExclusiveEnvironment().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment }
         */
        public List<EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment> getExclusiveEnvironment() {
            if (exclusiveEnvironment == null) {
                exclusiveEnvironment = new ArrayList<>();
            }
            return this.exclusiveEnvironment;
        }

        /**
         * stores specific exclusive environment details.
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "name",
                "classPaths"
        }, namespace = LoaderConstants.XSD_NAMESPACE)
        public static class ExclusiveEnvironment {

            @XmlElement(name = "Name", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
            protected String name;
            @XmlElement(name = "ClassPaths", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
            protected EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment.ClassPaths classPaths;

            /**
             * Gets the value of the name property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getName() {
                return name;
            }

            /**
             * Sets the value of the name property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Gets the value of the classPaths property.
             *
             * @return possible object is
             * {@link EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment.ClassPaths }
             */
            public EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment.ClassPaths getClassPaths() {
                return classPaths;
            }

            /**
             * Sets the value of the classPaths property.
             *
             * @param value allowed object is
             *              {@link EnvironmentConfiguration.ExclusiveEnvironments.ExclusiveEnvironment.ClassPaths }
             */
            public void setClassPaths(EnvironmentConfiguration.ExclusiveEnvironments
                                              .ExclusiveEnvironment.ClassPaths value) {
                this.classPaths = value;
            }

            /**
             * stores exclusive environment's class paths.
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "classPath"
            }, namespace = LoaderConstants.XSD_NAMESPACE)
            public static class ClassPaths {

                @XmlElement(name = "ClassPath", required = true, namespace = LoaderConstants.XSD_NAMESPACE)
                protected List<String> classPath;

                /**
                 * Gets the value of the classPath property.
                 * <p>
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the classPath property.
                 * <p>
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getClassPath().add(newItem);
                 * </pre>
                 * <p>
                 * <p>
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 */
                public List<String> getClassPath() {
                    if (classPath == null) {
                        classPath = new ArrayList<String>();
                    }
                    return this.classPath;
                }

            }

        }

    }

}
