package com.academy.solid.nie.server.config;


import com.academy.solid.nie.server.entity.GameEntity;
import com.academy.solid.nie.server.entity.PlayerEntity;
import com.academy.solid.nie.server.entity.Statement;
import com.academy.solid.nie.server.entity.Transcript;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {

  private static SessionFactory sessionFactory;

  private static SessionFactory buildSessionFactory() {
    return new Configuration()
        .configure("hibernate.cfg.xml")
        .addAnnotatedClass(GameEntity.class)
        .addAnnotatedClass(PlayerEntity.class)
        .addAnnotatedClass(Statement.class)
        .addAnnotatedClass(Transcript.class)
        .buildSessionFactory();

  }

  public static SessionFactory getSessionFactory() {
    if(sessionFactory == null) sessionFactory = buildSessionFactory();
    return sessionFactory;
  }

}
