package za.co.photo_sharing.app_ws.utility;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;
import java.util.Random;

public class UserIdentificationRandomGenerator implements IdentifierGenerator {

   @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {
        return getRandomNumberInts(25,100000);

    }


    public long getRandomNumberInts(int min, int max){
        Random random = new Random();
        return random.ints(min, (max + 1)).findFirst().getAsInt();
    }

  /*  @Override
    public void configure(Type type, Properties params,
                          ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);
        valuePrefix = ConfigurationHelper.getString(VALUE_PREFIX_PARAMETER,
                params, VALUE_PREFIX_DEFAULT);
        numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER,
                params, NUMBER_FORMAT_DEFAULT);
    }*/

}

