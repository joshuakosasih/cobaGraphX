package connector

import bl.core.neo.graph.db.{CategoryDB, CategoryDBImpl, UserDB, UserDBImpl}
import bl.core.neo.graph.model.converter.UserConverter
import bl.core.neo.graph.service.GraphService

import com.google.inject.AbstractModule

class DependencyInjection extends AbstractModule{
  @Override
  protected def configure(){
    bind(classOf[GraphService]).to(classOf[ConnectJanus])
    bind(classOf[UserConverter])
    bind(classOf[CategoryDB]).to(classOf[CategoryDBImpl])
    bind(classOf[UserDB]).to(classOf[UserDBImpl])
  }
}
