package net.sf.cotta.ftp;

import net.sf.cotta.test.Fixture;
import net.sf.cotta.test.FixtureType;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Fixture(FixtureType.ENVIRONMENT)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Ftp {
}
