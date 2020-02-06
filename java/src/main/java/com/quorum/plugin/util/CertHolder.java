package com.quorum.plugin.util;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

public class CertHolder {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X509CertificateHolder cert;

    public static CertHolder generate() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(4096);
        KeyPair keyPair = generator.generateKeyPair();

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(
                new X500Name("CN=localhost,O=quorum-plugin,OU=simple-security-manager.java"),
                BigInteger.valueOf(System.currentTimeMillis()),
                new Date(),
                new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)),
                new X500Name("CN=localhost,O=quorum-plugin,OU=simple-security-manager.java"),
                SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()))
        );
        X509CertificateHolder x509CertificateHolder = certBuilder.build(new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate()));

        CertHolder certHolder = new CertHolder();
        certHolder.cert = x509CertificateHolder;
        certHolder.privateKey = keyPair.getPrivate();
        certHolder.publicKey = keyPair.getPublic();
        return certHolder;
    }

    public byte[] keyPem() throws IOException {
        return toPem("RSA PRIVATE KEY", privateKey.getEncoded());
    }

    public byte[] certPem() throws IOException {
        return toPem("CERTIFICATE", cert.getEncoded());
    }

    public byte[] certDer() throws IOException {
        return cert.getEncoded();
    }

    private static byte[] toPem(String description, byte[] encoded) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PemWriter pemWriter = new PemWriter(new OutputStreamWriter(baos));
        pemWriter.writeObject(new PemObject(description, encoded));
        pemWriter.close();
        baos.close();
        return baos.toByteArray();
    }
}
