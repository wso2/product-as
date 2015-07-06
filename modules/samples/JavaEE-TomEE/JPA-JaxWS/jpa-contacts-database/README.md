## jpa-contacts-database sample

Please pay attention to the constrains defined in Contacts Entity when invoking addContact.

```
    @NotNull
    private String name;

    @Pattern(regexp = "\\(\\d{3}\\)\\d{3}-\\d{4}", message = "Invalid contact number")
    private String contactNumber;

    @Min(18)
    private int age;

    @Email
    private String email;

    @Past
    private Date birthday;
```

### Sample request for addContact

```
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:jax="http://jaxws.jpa.cdi.ee.sample.appserver.wso2.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <jax:addContact>
         <arg0>
            <age>26</age>
            <birthday>1988-11-29</birthday>
            <contactNumber>(077)650-9215</contactNumber>
            <email>kalpaw@wso2.com</email>
            <name>Kalpa Welivitigoda</name>
         </arg0>
      </jax:addContact>
   </soapenv:Body>
</soapenv:Envelope> 
```
