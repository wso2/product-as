<%@ page contentType="text/xml" %><%--
--%><?xml version="1.0" encoding="UTF-8"?>
<Arithmetics>
    <Arithmetic>
        <Expression>\${1}</Expression>
        <Value>${1}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${1 + 2}</Expression>
        <Value>${1 + 2}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${1.2 + 2.3}</Expression>
        <Value>${1.2 + 2.3}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${1.2E4 + 1.4}</Expression>
        <Value>${1.2E4 + 1.4}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${-4 - 2}</Expression>
        <Value>${-4 - 2}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${21 * 2}</Expression>
        <Value>${21 * 2}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${3/4}</Expression>
        <Value>${3/4}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${3 div 4}</Expression>
        <Value>${3 div 4}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${3/0}</Expression>
        <Value>${3/0}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${10%4}</Expression>
        <Value>${10%4}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${10 mod 4}</Expression>
        <Value>${10 mod 4}</Value>
    </Arithmetic>
    <Arithmetic>
        <Expression>\${(1==2) ? 3 : 4}</Expression>
        <Value>${(1==2) ? 3 : 4}</Value>
    </Arithmetic>
</Arithmetics>