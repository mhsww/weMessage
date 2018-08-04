package scott.wemessage.server.security;

import org.apache.commons.codec.binary.Base64;

import scott.wemessage.commons.crypto.Base64Wrapper;

public class ServerBase64Wrapper extends Base64Wrapper {

    @Override
    public byte[] decodeString(String string) {
        return Base64.decodeBase64(string);
    }

    @Override
    public String encodeToString(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
}