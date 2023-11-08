package com.spayindia.app.util

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader

object PidParser2 {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(xml: String?): Array<String> {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xmlPullParser = factory.newPullParser()
        xmlPullParser.setInput(StringReader(xml))
        /*    xmlPullParser.setInput(new StringReader("<?xml version=\"1.0\"?>\n" +
                "<PidData>\n" +
                "  <Resp errCode=\"0\" errInfo=\"Success\" fCount=\"1\" fType=\"2\" nmPoints=\"30\" qScore=\"72\" />\n" +
                "  <DeviceInfo dpId=\"MANTRA.MSIPL\" rdsId=\"MANTRA.WIN.001\" rdsVer=\"1.0.0\" mi=\"MFS100\" mc=\"MIIEGzCCAwOgAwIBAgIIdkuCckhUIJMwDQYJKoZIhvcNAQELBQAwgekxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgNTFNMEsGA1UEMxNEQiAyMDMgU2hhcGF0aCBIZXhhIG9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTIEcgSGlnaHdheSBBaG1lZGFiYWQxEjAQBgNVBAkTCUFobWVkYWJhZDEQMA4GA1UECBMHR3VqYXJhdDESMBAGA1UECxMJVGVjaG5pY2FsMSUwIwYDVQQKExxNYW50cmEgU29mdGVjaCBJbmRpYSBQdnQgTHRkMQswCQYDVQQGEwJJTjAeFw0xOTA5MTkwNjIyMjJaFw0xOTEwMTkwNjM3MDRaMIGwMSQwIgYJKoZIhvcNAQkBFhVzdXBwb3J0QG1hbnRyYXRlYy5jb20xCzAJBgNVBAYTAklOMRAwDgYDVQQIEwdHVUpBUkFUMRIwEAYDVQQHEwlBSE1FREFCQUQxDjAMBgNVBAoTBU1TSVBMMR4wHAYDVQQLExVCaW9tZXRyaWMgTWFudWZhY3R1cmUxJTAjBgNVBAMTHE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC2m2w5Pl17jMLPoNfC4mqg2xUSYJ9pg/PBzP7FrfKiNNT8lrzCD0jzqcv0CMnilm1FL/LSBOpZGOTr8GeofFNApTlasVOC83/keMUyc+xp1+STXcdlZn1y7x1175T/WcKmXmUxcwTPSaaLtn1kdnX5ZM/P1pMDS7zySLU9mrg0ygNTO/oIWf2OX/tpiqzD3J7VnInXt5vzyk79lxnoKOp75qqmwJ3rPLqPWyxf4lCXlcvnkj/hb4tPIsJ1YfTENdJCCD4C4AmqiqeGhehjMzoT3ZMo+EpOOw/zpw27e5q59DX98dVm04JL9TtkiZocLG+rqIGqjLLZOr95ClbWaPPfAgMBAAEwDQYJKoZIhvcNAQELBQADggEBADp4aanFr/Cx01HwlZTHyVEaYTF2jkv/FkzfIPHFrcem+24Q3cD5iBoBaQFsKZoer3C8YJgyAkgVsc0PQ4MdkcSQRnVYr03sgRIdQVrXHG0en255x4zkKpMY7MykyD8TISRzFVTFxUJrQHzycwp7umwjd0M3SaPH0qcarJs0lX0d46L/JghwbyCnUtMW8ldzxVxE1YkdJjlrvLVMwcFLvchvAIanRMGQD4hfDWvFd398pFUNKp7wLD6njN8Wgb5cueSsk31KcI2viOkoXnlfWMkwQN/+RxnS4ukJeARDE1N4MJIa9WaO7yKMqPn9pBYEnatS7gkiGMSd/c7kNaH3Prc=\" dc=\"e769e1c9-dafd-441d-877b-277b7a05419c\">\n" +
                "    <additional_info>\n" +
                "      <Param name=\"srno\" value=\"0401665\" />\n" +
                "      <Param name=\"sysid\" value=\"6A7FEBF307F6E5EFBFF0\" />\n" +
                "      <Param name=\"ts\" value=\"2019-09-21T11:42:16+05:30\" />\n" +
                "    </additional_info>\n" +
                "  </DeviceInfo>\n" +
                "  <Skey ci=\"20191230\">hzOSFgRJCuCQxg94mIe6Zlk31UbKus3KkgqKnkZKwo3pR/riA8lkmR+hEUhmDtbOrJyxeCEUwjPgO2jWaInyG92fml1K9xxMmdvoikJhlKTgOCFjG2kvEzly6xiYMLO9dX9OnqrZ+rC1vx1w5NMUKoSjTy2jhx/e+vtVvC2jBm+MYJI/61vIVmLn+JAECu//wuy5ctvBn84z6LoZqwBW5rdoEsHhO5T/wqdc/DJBukXyOWHlrFEjcTPnetARonx4ef9g1R2BZ2KjJ/6zoGOCaQgoXeKigHD3beGWBeLQCvVNi1NUbVjSPJNMZg6trdg/0wYBOutX1rJDXYoSzKX23Q==</Skey>\n" +
                "  <Hmac>kPdmlc5aSDFbrcy2eWlNhM3vabjqKuMPqpQlqUgadG7hi3DlFN6w8IdoFJWn0nkP</Hmac>\n" +
                "  <Data type=\"X\">MjAxOS0wOS0yMVQxMTo0MjoxNgKJgrYKDr09F+ojJGa5+oLIGDkP0xrm2vvF2LsZ7gFcNM7XPwL7N3zYrA+CePz/D8gdErQnRbxwvCqVLNEq6z/huiL3kBeMJaqzRTkuQZ9EWB2UeCbC+FzGWlmQ6zh+x3Bt6+kI/YP30Te20KjheHyr7rcrlZtLuLJREjtmYF5wz9kCZwkCXWySBYjx5qM4HAyGOGCswVaXD1kiTKzp8Wb/5CKnU6ehDKRS+eJ02qkYH7VJZdR9dCCvWxrV/IGXnsK+rsPta3RYlRICIOyUBB5uWYJ30lxng2ID2FpN0cGvWlHN+EDfSdnSiZDJP3H8HpemwJKge2egoElvrq5nIsGlTcjJHe8QXn+gt5w+5KsgfUGuFB4bH6hNlH8RAiruOp7HcMRSR+Sg0II0v+FXPLaiUUt56EgOB0V11YZBnKmLcH+vaA0U+Q+Q/IhU4RC90vblO6/AN2q7NTwLwVYcEwT2CpR05Mq+5qGJP+Kasftt1pfcJpq791dDEblfqmyv1VZHijruJ10Hon3JT+gyE6bde1k/oWZ+e7XJAwJBeWWesi7W2fMKFL9/TLFg6B2F/5Aa3aHsgitVJp0q3oHHhIAfYd7euVpy9SGZl406tPM/QxPliy9XJmNx5ZSCkCG9UAyWHSk7HQmuBjqJdQO4poz0EbniVUkkWb/YDlTGDnZtfBY7C1M/BeW+EZc5AlFeplt1NhzNJHLP7elsNIb54/2k7gDkNfgdYI+hL8HFvvVbTf1WOKIjymkJmCCubli/n79Udv0DSkb48TLwVXtRPZlBOhXjsG22M5oiGg7QE4qmLDTPaGKx9vYF5gl5Opogq2O0lHrv9BPOXLg/4645kgcWJvza7aqSf/54Cul/1TUz0n8Gcbh0KwJGP6ZhRxQ0bfg6seSzMAhr4jlczcORPI8XOpviY13z/0Q+PK/pq0LEo6+X0LuwxwOCpi1srGyyiOK01gGh3t6F5aD3PTPxXEAJd8AEtw+NXX3aZ+8wPkDvfBcCoOl9eP5xv7vZ3/Zp0ycy5qX8o+9ys7uTIR4JBUUeFzCs1/Q1eiFIl/Wuz/HomUKTsNZKWoDvM2+wiuQxZ4uR67V/h1fkdMC7TnMZOH43h9XZvw==</Data>\n" +
                "</PidData>"));*/
        val respStrings = arrayOf("na", "na")
        var eventType = xmlPullParser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                println("Start document")
            } else if (eventType == XmlPullParser.START_TAG) {
                if (xmlPullParser.name.equals("Resp", ignoreCase = true)) {
                    val count = xmlPullParser.attributeCount
                    for (i in 0 until count) {
                        val attributeName = xmlPullParser.getAttributeName(i)
                        println(attributeName)
                        if (attributeName.equals("errCode", ignoreCase = true)) {
                            respStrings[0] = xmlPullParser.getAttributeValue(i)
                            println("errCode : " + xmlPullParser.getAttributeValue(i))
                        }
                        if (attributeName.equals("errInfo", ignoreCase = true)) {
                            respStrings[1] = xmlPullParser.getAttributeValue(i)
                            println("errInfo : " + xmlPullParser.getAttributeValue(i))
                        }
                    }
                }
            }
            eventType = xmlPullParser.next()
        }
        return respStrings
    }

    fun getDeviceSerialNumber(data : String) : String {

        var serialNumber = ""
        /*       val data = "<PidData>\n" +
                       "\t<Data type=\"X\">MjAyMC0wMi0yN1QxMTozMDo0NEcVECg7/1YAkW5r53BIyynu0d35Iupus3Ox3T2ampeL3vH8SaFonD7THhxelID4Ewa31qY2zyJPoP9pOCEJ1jOJAe30azmgonZne2XH049Zqfrfwpgg/YuY8zF231T57nNeMlMWkeKjmgdh+GAYpip+XkEKGEzmNmMEMo92isjl/Oxqh7UQZGq+JerHzuPFEP9ZGs9rG9vghgnduvxyv0bKGXtbGEhVjMUQvSIJgD1VGyxBInC0ToXGe7Q/kSzD+s1tsGeUB/aE9JDHOuDKogvCKi9nDrwkoy8rMQ4eBFSMXXyW3/zTWqPtFkkiNHpEnF62y1X+8T2VYqqavqgxBgeogKMyFgjL4ic6q8nFnaBc5LJSlkcL8Bybf4Znu2SvzIxupHLjA+qS4tZRvv5sSe5/JzXX0Fib+BLPXmO8KLyNQVAJ7D84unFVwgvZH11vXjsPLVNPOriFtQxjBXSG7kzuCXzqEG9snAb3LkESwF4p7042Z0HKEVrhwbNHozVdFanI5sdeseNiMXsy6B8axCcXCfRVMkaS8xgs0wAceK86qjct0X8gz0DLteOLLGZlNC4VNzA2vP856ivndK+5xDSdWUOz6d1pc2QC8+/w8Gst9giQUn7IVvDuxEVwuE+PRYWD+AB8UwPKygtRqTAD0GfU+iYXnzaUJZlRs2Hcq8w60JorhAEin183ZTTQZV7LgU3K7Ae2pjuvyOUxIqLYDS4HxTI4k3dNLs09z6ac2nVYPhigTsgnVtUNNucocjve263ocz+N9+xnolG/dWZ+dyEczXP3HjXezXXTRC3t+Tpz+HiV1gDNrUnufRkZdpcqIMd3nu8441WvBlbwrJo5x0gRdfoSfUSWzRIzNu5KAir36tEdoF7PIgo7GENAjR7KfwKn+4OUmCYmDBgOVavx6LOoizsLc45IXUOghNr15405Ksau92vBKaQpxqdfFv1k05Go/RCROoe/NAMhnaDOKFQzU2n4t5Cseb/YSkcMKftGQlHaQ5MmLZq3dLQwMGyg714O0EhaiO7bJw5yyWm3pP91EVUWXnfKhMXmdo5fpsn37HvJRLkli1I1Oc5SEl2ryp/EbzNou0ivk+6SICZTuCJmfgSmKLRBvaVYG1bMIdn8YWHEEEr0pHbcKuQ2L9gROk=</Data>\n" +
                       "\t<DeviceInfo dc=\"e769e1c9-dafd-441d-877b-277b7a05419c\" dpId=\"MANTRA.MSIPL\" mc=\"MIIEGTCCAwGgAwIBAgIGAXCFA3nhMA0GCSqGSIb3DQEBCwUAMIHpMSowKAYDVQQDEyFEUyBNYW50cmEgU29mdGVjaCBJbmRpYSBQdnQgTHRkIDUxTTBLBgNVBDMTREIgMjAzIFNoYXBhdGggSGV4YSBvcHBvc2l0ZSBHdWphcmF0IEhpZ2ggQ291cnQgUyBHIEhpZ2h3YXkgQWhtZWRhYmFkMRIwEAYDVQQJEwlBaG1lZGFiYWQxEDAOBgNVBAgTB0d1amFyYXQxEjAQBgNVBAsTCVRlY2huaWNhbDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjAwMjI3MDQ0NDIzWhcNMjAwMzI4MDQ1OTIxWjCBsDEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjEQMA4GA1UECBMHR1VKQVJBVDESMBAGA1UEBxMJQUhNRURBQkFEMQ4wDAYDVQQKEwVNU0lQTDEeMBwGA1UECxMVQmlvbWV0cmljIE1hbnVmYWN0dXJlMSUwIwYDVQQDExxNYW50cmEgU29mdGVjaCBJbmRpYSBQdnQgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1GWTpsD2Yxbk7/j7fThOJPIYTfLib+3fcfNab3FsX2rHNfFfKRMwdyUgaIYesbNvkVyXR4CrL/531xSB0NjR1T55okGbt2/9Hw4DK/yaeAN81FfDhqpN22jOrTib4ROU9bhAfvUX/hJNLl5CjqDk4ZQF+CZgc1eFl9TqmqjrvwmnIwFJDL7l2aaPFhXSoLl2Cl+hG+rUV0WASKfRjhPZQGJEmBYmJtWs4gVjWltc/YkXN4l3YlYYxVj6x+qMWq062To9K+nbovtfepPINmRL2Qgi7Mh6HygDMAJVRyzYuLQbAcg0X6J238MeNT+YMYjs1wFAGpZDj3O5rperd0k/RwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQAnLjDaAfuztjvFVjcIR+SFC97Nw9+Fx1kyf1QRAzMC+ioq9KkQ9sTDvITrAsj4It97Cqv9EyfvuG8hUeY4haJntHmiytC5QvvB5qONNH0uYC6p9gHF3+yvAzcqrLMw0qGN3C+ZL+Bvjg/oWgllbzY/Aj1VcDflBKbOI4LrRHjhCaS2YCfVEbG/bSIBPfHZAgiH63o+WrQ1hTa7h1Pf23tWsdx8wZ0q31kI0zHZA8/C4BDYrfHeijbKiixIt7NEcAHUjTkurddC1jVXSPHDuGu0von8yiTcT0TKcWohdt1zdMs6p6pU/TEJ/PyScfubKA49owUo1J9Z85lheuyubWNC\" mi=\"MFS100\" rdsId=\"MANTRA.AND.001\" rdsVer=\"1.0.3\">\n" +
                       "\t\t<additional_info>\n" +
                       "\t\t\t<Param name=\"srno\" value=\"0401665\"/>\n" +
                       "\t\t\t<Param name=\"sysid\" value=\"351893086845933\"/>\n" +
                       "\t\t\t<Param name=\"ts\" value=\"2020-02-27T11:30:48+05:30\"/>\n" +
                       "\t\t</additional_info>\n" +
                       "\t</DeviceInfo>\n" +
                       "\t<Hmac>0YHveQ1toht6PDG/XMJjlJM0nsDS/m6T/c1qrnx83t6P3o6ar+pgWDAR1hTJ1S0Mn</Hmac>\n" +
                       "\t<Resp errCode=\"0\" errInfo=\"Capture Success\" fCount=\"1\" fType=\"2\" iCount=\"0\" iType=\"0\" nmPoints=\"36\" pCount=\"0\" pType=\"0\" qScore=\"68\"/>\n" +
                       "\t<Skey ci=\"20221021\">S3zjddslj3O4FtaF4U/i9OHbQ4k9K5JASa3HIr+EgbpzKFwyIaEbXQ2OS9gyHunZl6o7Npu4rdz9nTu6oAKnUsFIt+3M8nXYlGvkrMR3dBMvKcsQ5UeWvahJ7ZWYau2sIOiwcdcNbi1sHV1MFDE6LuKzVnCuVZsq/aSd/MxgxHShhFHTY6i9kb0kMFdCaeCw9Pqbmb4nPuUPsyXJhXlzbYADgjNGHoJq7ke4fEnN9m5FFxrmpcFKVheuLf4zKB2lx0tUF2befkB5xJC7pEoSZAi4RWvp0VoOwiCFqIoq+MFXcdLZ0wPnxapvZJEU2ifUoRhMgkAMi5VBa2j06q4HclZHJw==</Skey>\n" +
                       "</PidData>\n" +
                       "\n" +
                       "\n"*/
        try {
            val factory =
                XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(StringReader(data))
            var eventType = xpp.eventType

            var isPidDataTag = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_DOCUMENT -> {

                    }
                    XmlPullParser.START_TAG -> {
                        if(xpp.name=="Data") isPidDataTag = true
                    }
                    XmlPullParser.END_TAG -> {
                        if(xpp.name == "Param"){
                            val name = xpp.getAttributeValue(null,"name")
                            if(name == "srno"){
                                serialNumber = xpp.getAttributeValue(null,"value");
                            }
                        }

                    }
                    XmlPullParser.TEXT -> {
                        /*if(isPidDataTag) {
                            AppLog.d("pidata : ${xpp.text}")
                            isPidDataTag = !isPidDataTag
                        }*/
                    }
                }
                eventType = xpp.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        finally {
            return serialNumber
        }
    }
}