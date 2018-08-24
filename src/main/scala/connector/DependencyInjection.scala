package connector

import bl.core.neo.graph.db.CategoryDB
import bl.core.neo.graph.service.GraphService
import com.google.inject.AbstractModule
import service.GraphServiceImpl

class DependencyInjection extends AbstractModule{
  @Override
  protected def configure(){
    bind(classOf[GraphService]).to(classOf[GraphServiceImpl])
    bind(classOf[ConnectJava])
  }
}
